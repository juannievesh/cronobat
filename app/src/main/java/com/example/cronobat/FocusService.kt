package com.example.cronobat

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.provider.Settings
import android.os.Handler
import android.os.Looper
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.Gravity
import android.view.View
import android.widget.TextView

class FocusService : Service() {

    companion object {
        const val CHANNEL_ID = "focus_channel"
        const val NOTIF_ID = 1001
        const val ACTION_UPDATE = "com.example.cronobat.UPDATE"
        const val EXTRA_TITLE = "title"
        const val EXTRA_TEXT = "text"
        const val EXTRA_IS_FOCUS = "is_focus"
    }

    private var lastTitle: String = ""
    private var lastText: String = ""
    private var lastIsFocus: Boolean = true

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lastTitle = intent?.getStringExtra(EXTRA_TITLE) ?: getString(R.string.app_name)
        lastText = intent?.getStringExtra(EXTRA_TEXT) ?: ""
        lastIsFocus = intent?.getBooleanExtra(EXTRA_IS_FOCUS, true) ?: true
        val notification = buildNotification(lastTitle, lastText)
        startForeground(NOTIF_ID, notification)
        ensureUsageAccess()
        startMonitoring()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, text: String): Notification {
        val openIntent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pi)
            .build()
    }

    // Usage access and monitoring
    private val handler = Handler(Looper.getMainLooper())
    private var lastEventTime = 0L
    private val entertainmentPackages = setOf(
        "com.google.android.youtube",
        "com.netflix.mediaclient",
        "com.instagram.android",
        "com.facebook.katana",
        "com.zhiliaoapp.musically",
        "com.twitter.android",
        "com.reddit.frontpage",
        "com.snapchat.android",
        "com.pinterest",
        "org.telegram.messenger",
        "com.whatsapp",
        "com.instagram.barcelona" // Threads
    )
    private var lastWarnTimeMs: Long = 0L

    private fun hasUsageAccess(): Boolean {
        val appOps = Settings.Secure.getInt(contentResolver, "usage_stats_enabled", 0)
        return appOps == 1
    }

    private fun ensureUsageAccess() {
        // Only check once and don't repeatedly try to open settings
        if (!hasUsageAccess()) {
            // Log that usage access is needed but don't auto-open settings
            // User can manually grant it when needed
        }
    }

    private fun startMonitoring() {
        handler.removeCallbacksAndMessages(null)
        handler.post(object : Runnable {
            override fun run() {
                try {
                    val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                    val end = System.currentTimeMillis()
                    val begin = if (lastEventTime == 0L) end - 2000 else lastEventTime
                    val events: UsageEvents = usm.queryEvents(begin, end)
                    val event = UsageEvents.Event()
                    while (events.hasNextEvent()) {
                        events.getNextEvent(event)
                        lastEventTime = maxOf(lastEventTime, event.timeStamp)
                        if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                            val pkg = event.packageName ?: continue
                            if (entertainmentPackages.contains(pkg) && lastIsFocus) {
                                val now = System.currentTimeMillis()
                                if (now - lastWarnTimeMs > 4000) {
                                    lastWarnTimeMs = now
                                    warnEntertainment(pkg)
                                }
                            }
                        }
                    }
                } catch (_: Exception) {}
                handler.postDelayed(this, 1500)
            }
        })
    }

    private fun warnEntertainment(pkg: String) {
        val appName = try {
            val pm = packageManager
            val ai = pm.getApplicationInfo(pkg, 0)
            pm.getApplicationLabel(ai).toString()
        } catch (_: Exception) { pkg }

        // Try system overlay first
        if (Settings.canDrawOverlays(this)) {
            showSystemOverlay()
        } else {
            // Fallback to full-screen activity
            val intent = Intent(this, DistractionOverlayActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(EXTRA_TITLE, lastTitle)
            intent.putExtra(EXTRA_TEXT, lastText)
            startActivity(intent)
        }
    }

    private var overlayView: View? = null
    private fun showSystemOverlay() {
        if (overlayView != null) return
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.overlay_focus_dot, null)
        overlayView = view

        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            android.graphics.PixelFormat.TRANSLUCENT
        )
        lp.gravity = Gravity.TOP or Gravity.START

        // Minimal info with dot matrix
        val tvMode = view.findViewById<TextView>(R.id.tvOverlayMode)
        val tvTime = view.findViewById<TextView>(R.id.tvOverlayTime)
        // Use the latest values pushed by the activity
        tvMode.text = lastTitle
        // If lastText contains phase • time, keep as-is; otherwise it's just time
        tvTime.text = lastText

        wm.addView(view, lp)

        // Redirect back to app shortly and then remove overlay
        handler.postDelayed({
            val i = Intent(this, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            removeOverlay()
        }, 600)
    }

    private fun removeOverlay() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView?.let {
            try { wm.removeView(it) } catch (_: Exception) {}
            overlayView = null
        }
    }
}


