package com.example.cronobat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class DistractionOverlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Cronobat)
        setContentView(R.layout.activity_distraction_overlay)

        // Try to reflect current focus info if provided
        val title = intent?.getStringExtra(FocusService.EXTRA_TITLE)
        val text = intent?.getStringExtra(FocusService.EXTRA_TEXT)
        if (title != null || text != null) {
            findViewById<TextView?>(R.id.tvOverlayTitle)?.text = title ?: getString(R.string.app_name)
            findViewById<TextView?>(R.id.tvOverlaySubtitle)?.text = text ?: ""
        }

        // Redirect back to app after a brief moment
        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(i)
            finish()
        }, 600)
    }
}


