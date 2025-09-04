

package com.example.cronobat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.HapticFeedbackConstants
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.widget.Toast
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.cronobat.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    // Timer variables
    private var isTimerRunning = false
    private var isStopwatchRunning = false
    private var currentTimeInSeconds = 0L
    private var timerDuration = 0L
    private var countDownTimer: CountDownTimer? = null
    private var stopwatchHandler = Handler(Looper.getMainLooper())
    private var stopwatchRunnable: Runnable? = null
    
    // Background music variables
    private var isMusicPlaying = false
    private var currentMusicType = "rain"

    // Modes
    private enum class Mode { TIMER, STOPWATCH, POMODORO }
    private var currentMode: Mode = Mode.POMODORO

    // Pomodoro
    private enum class PomodoroPhase { FOCUS, BREAK }
    private var pomodoroPhase: PomodoroPhase = PomodoroPhase.FOCUS
    private var pomodoroFocusMinutes = 25
    private var pomodoroBreakMinutes = 5
    private var pomodoroTimer: CountDownTimer? = null
    private var isPomodoroRunning = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupListeners()
    }
    
    private fun setupUI() {
        // Default to Pomodoro mode
        setMode(Mode.POMODORO)
        updateTimeDisplay(0)
        
        // Set initial seekbar values
        binding.seekBarTimer.max = 3600 // 1 hour max
        binding.seekBarTimer.progress = 0
    }
    
    private fun setupListeners() {
        // Mode chooser
        binding.fabMode?.setOnClickListener {
            showModeChooser()
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }

        // Timer controls
        binding.btnStartTimer.setOnClickListener {
            if (!isTimerRunning) {
                startTimer()
            } else {
                pauseTimer()
            }
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        
        binding.btnResetTimer.setOnClickListener {
            resetTimer()
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        
        // Stopwatch controls
        binding.btnStartStopwatch.setOnClickListener {
            if (!isStopwatchRunning) {
                startStopwatch()
            } else {
                pauseStopwatch()
            }
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        
        binding.btnResetStopwatch.setOnClickListener {
            resetStopwatch()
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        
        // Music controls
        binding.fabMusic?.setOnClickListener {
            showMusicSettings()
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        
        // SeekBar for timer duration
        binding.seekBarTimer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isTimerRunning) {
                    timerDuration = progress.toLong()
                    updateTimeDisplay(timerDuration)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Pomodoro controls
        binding.btnStartPomodoro?.setOnClickListener {
            if (!isPomodoroRunning) startPomodoro() else pausePomodoro()
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        binding.btnResetPomodoro?.setOnClickListener {
            resetPomodoro()
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        binding.seekFocus?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val min = if (progress < 1) 1 else progress
                pomodoroFocusMinutes = min
                binding.tvFocusValue?.text = "$min min"
                if (!isPomodoroRunning && currentMode == Mode.POMODORO) {
                    updateTimeDisplay((pomodoroFocusMinutes * 60).toLong())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.seekBreak?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val min = if (progress < 1) 1 else progress
                pomodoroBreakMinutes = min
                binding.tvBreakValue?.text = "$min min"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    private fun startTimer() {
        if (timerDuration <= 0) {
            Toast.makeText(this, "Establece un tiempo primero", Toast.LENGTH_SHORT).show()
            return
        }
        
        isTimerRunning = true
        currentTimeInSeconds = timerDuration
        updateTimeDisplay(currentTimeInSeconds)
        
        countDownTimer = object : CountDownTimer(timerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTimeInSeconds = millisUntilFinished / 1000
                updateTimeDisplay(currentTimeInSeconds)
                updateFocusNotification()
            }
            
            override fun onFinish() {
                isTimerRunning = false
                currentTimeInSeconds = 0
                updateTimeDisplay(0)
                updateTimerButton()
                Toast.makeText(this@MainActivity, "¡Tiempo completado!", Toast.LENGTH_LONG).show()
                stopFocusNotification()
            }
        }.start()
        
        updateTimerButton()
        startFocusNotification("Temporizador", formatRemaining(currentTimeInSeconds))
    }
    
    private fun pauseTimer() {
        isTimerRunning = false
        countDownTimer?.cancel()
        updateTimerButton()
    }
    
    private fun resetTimer() {
        isTimerRunning = false
        countDownTimer?.cancel()
        currentTimeInSeconds = 0
        timerDuration = 0
        binding.seekBarTimer.progress = 0
        updateTimeDisplay(0)
        updateTimerButton()
    }
    
    private fun startStopwatch() {
        isStopwatchRunning = true
        currentTimeInSeconds = 0
        
        stopwatchRunnable = object : Runnable {
            override fun run() {
                if (isStopwatchRunning) {
                    currentTimeInSeconds++
                    updateTimeDisplay(currentTimeInSeconds)
                    updateFocusNotification()
                    stopwatchHandler.postDelayed(this, 1000)
                }
            }
        }
        
        stopwatchHandler.post(stopwatchRunnable!!)
        updateStopwatchButton()
        startFocusNotification("Cronómetro", formatRemaining(currentTimeInSeconds))
    }
    
    private fun pauseStopwatch() {
        isStopwatchRunning = false
        stopwatchRunnable?.let { stopwatchHandler.removeCallbacks(it) }
        updateStopwatchButton()
        stopFocusNotification()
    }
    
    private fun resetStopwatch() {
        isStopwatchRunning = false
        stopwatchRunnable?.let { stopwatchHandler.removeCallbacks(it) }
        currentTimeInSeconds = 0
        updateTimeDisplay(0)
        updateStopwatchButton()
        stopFocusNotification()
    }
    
    private fun toggleBackgroundMusic() {
        isMusicPlaying = !isMusicPlaying
        
        if (isMusicPlaying) {
            Toast.makeText(this, "Música de fondo iniciada: $currentMusicType", Toast.LENGTH_SHORT).show()
            // TODO: Implement actual music playback
        } else {
            Toast.makeText(this, "Música de fondo pausada", Toast.LENGTH_SHORT).show()
            // TODO: Implement music pause
        }
    }
    
    private fun changeBackgroundMusic() {
        val musicTypes = listOf("rain", "fire", "forest", "ocean", "white_noise")
        val currentIndex = musicTypes.indexOf(currentMusicType)
        val nextIndex = (currentIndex + 1) % musicTypes.size
        currentMusicType = musicTypes[nextIndex]
        
        Toast.makeText(this, "Música cambiada a: $currentMusicType", Toast.LENGTH_SHORT).show()
        
        // TODO: Implement music change
    }
    
    private fun setMode(mode: Mode) {
        currentMode = mode
        // Hide all
        binding.timerLayout?.visibility = View.GONE
        binding.stopwatchLayout?.visibility = View.GONE
        binding.pomodoroLayout?.visibility = View.GONE
        // Show selected
        when (mode) {
            Mode.TIMER -> {
                binding.timerLayout?.visibility = View.VISIBLE
                binding.tvModeTitle?.text = getString(R.string.mode_timer)
                updateTimeDisplay(timerDuration)
            }
            Mode.STOPWATCH -> {
                binding.stopwatchLayout?.visibility = View.VISIBLE
                binding.tvModeTitle?.text = getString(R.string.mode_stopwatch)
                updateTimeDisplay(currentTimeInSeconds)
            }
            Mode.POMODORO -> {
                binding.pomodoroLayout?.visibility = View.VISIBLE
                binding.tvModeTitle?.text = getString(R.string.mode_pomodoro)
                pomodoroPhase = PomodoroPhase.FOCUS
                updateTimeDisplay((pomodoroFocusMinutes * 60).toLong())
            }
        }
    }

    private fun showModeChooser() {
        val items = arrayOf(
            getString(R.string.mode_timer),
            getString(R.string.mode_stopwatch),
            getString(R.string.mode_pomodoro)
        )
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.mode_title))
            .setItems(items) { _, which ->
                // Stop any running tasks
                pauseTimer(); resetTimer()
                pauseStopwatch(); resetStopwatch()
                pausePomodoro(); resetPomodoro()
                when (which) {
                    0 -> setMode(Mode.TIMER)
                    1 -> setMode(Mode.STOPWATCH)
                    2 -> setMode(Mode.POMODORO)
                }
            }
            .show()
    }
    
    private fun updateTimeDisplay(seconds: Long) {
        val hours = TimeUnit.SECONDS.toHours(seconds)
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val secs = seconds % 60
        
        val timeString = if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
        
        binding.tvTimeDisplay?.text = timeString
    }
    
    private fun updateTimerButton() {
        binding.btnStartTimer?.text = if (isTimerRunning) "Pausar" else "Iniciar"
    }
    
    private fun updateStopwatchButton() {
        binding.btnStartStopwatch?.text = if (isStopwatchRunning) "Pausar" else "Iniciar"
    }
    
    private fun showMusicSettings() {
        val options = arrayOf(
            if (isMusicPlaying) "Pausar Música" else "Iniciar Música",
            "Cambiar tipo: $currentMusicType",
            "Configurar volumen"
        )
        
        AlertDialog.Builder(this)
            .setTitle("Configuración de Música")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> toggleBackgroundMusic()
                    1 -> changeBackgroundMusic()
                    2 -> Toast.makeText(this, "Configuración de volumen próximamente", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    // Pomodoro logic
    private fun startPomodoro() {
        if (isPomodoroRunning) return
        isPomodoroRunning = true
        val durationSec = if (pomodoroPhase == PomodoroPhase.FOCUS) pomodoroFocusMinutes * 60 else pomodoroBreakMinutes * 60
        currentTimeInSeconds = durationSec.toLong()
        updateTimeDisplay(currentTimeInSeconds)
        pomodoroTimer = object : CountDownTimer(currentTimeInSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTimeInSeconds = millisUntilFinished / 1000
                updateTimeDisplay(currentTimeInSeconds)
                updateFocusNotification()
            }
            override fun onFinish() {
                isPomodoroRunning = false
                // Switch phase
                pomodoroPhase = if (pomodoroPhase == PomodoroPhase.FOCUS) PomodoroPhase.BREAK else PomodoroPhase.FOCUS
                val next = if (pomodoroPhase == PomodoroPhase.FOCUS) pomodoroFocusMinutes else pomodoroBreakMinutes
                Toast.makeText(this@MainActivity, if (pomodoroPhase == PomodoroPhase.FOCUS) getString(R.string.phase_focus) else getString(R.string.phase_break), Toast.LENGTH_SHORT).show()
                currentTimeInSeconds = (next * 60).toLong()
                updateTimeDisplay(currentTimeInSeconds)
                startPomodoro()
            }
        }.start()
        updatePomodoroButtons()
        startFocusNotification("Pomodoro", phaseLabel() + " • " + formatRemaining(currentTimeInSeconds))
    }

    private fun pausePomodoro() {
        isPomodoroRunning = false
        pomodoroTimer?.cancel()
        updatePomodoroButtons()
        stopFocusNotification()
    }

    private fun resetPomodoro() {
        pausePomodoro()
        pomodoroPhase = PomodoroPhase.FOCUS
        currentTimeInSeconds = (pomodoroFocusMinutes * 60).toLong()
        updateTimeDisplay(currentTimeInSeconds)
        updatePomodoroButtons()
        stopFocusNotification()
    }

    private fun phaseLabel(): String = if (pomodoroPhase == PomodoroPhase.FOCUS) getString(R.string.phase_focus) else getString(R.string.phase_break)

    private fun formatRemaining(seconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(seconds)
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val secs = seconds % 60
        return if (hours > 0) String.format("%02d:%02d:%02d", hours, minutes, secs) else String.format("%02d:%02d", minutes, secs)
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, start the service
            startFocusService()
        } else {
            Toast.makeText(this, "Se necesita permiso de notificaciones para el modo enfoque", Toast.LENGTH_LONG).show()
        }
    }

    private fun startFocusNotification(title: String, text: String) {
        // Store the notification data
        pendingNotificationTitle = title
        pendingNotificationText = text
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check notification permission for Android 13+
            when {
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    startFocusService()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale and request permission
                    showNotificationPermissionDialog()
                }
                else -> {
                    // Request permission
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For older versions, start service directly
            startFocusService()
        }
    }

    private var pendingNotificationTitle: String = ""
    private var pendingNotificationText: String = ""

    private fun startFocusService() {
        val intent = Intent(this, FocusService::class.java)
        intent.putExtra(FocusService.EXTRA_TITLE, pendingNotificationTitle)
        intent.putExtra(FocusService.EXTRA_TEXT, pendingNotificationText)
        // Flag to indicate we are in focus mode whenever a timer/stopwatch/pomodoro is running
        val isFocus = when (currentMode) {
            Mode.TIMER -> isTimerRunning
            Mode.STOPWATCH -> isStopwatchRunning
            Mode.POMODORO -> isPomodoroRunning && pomodoroPhase == PomodoroPhase.FOCUS
        }
        intent.putExtra(FocusService.EXTRA_IS_FOCUS, isFocus)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permiso de Notificaciones")
            .setMessage("Cronobat necesita mostrar notificaciones para mantener el modo enfoque activo cuando la app esté en segundo plano.")
            .setPositiveButton("Permitir") { _, _ ->
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Cancelar") { _, _ ->
                Toast.makeText(this, "Modo enfoque desactivado", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun updateFocusNotification() {
        val title = when (currentMode) {
            Mode.TIMER -> "Temporizador"
            Mode.STOPWATCH -> "Cronómetro"
            Mode.POMODORO -> "Pomodoro"
        }
        val text = if (currentMode == Mode.POMODORO) phaseLabel() + " • " + formatRemaining(currentTimeInSeconds) else formatRemaining(currentTimeInSeconds)
        startFocusNotification(title, text)
    }

    private fun stopFocusNotification() {
        stopService(Intent(this, FocusService::class.java))
    }

    private fun updatePomodoroButtons() {
        binding.btnStartPomodoro?.text = if (isPomodoroRunning) getString(R.string.pause_pomodoro) else getString(R.string.start_pomodoro)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        stopwatchRunnable?.let { stopwatchHandler.removeCallbacks(it) }
    }
}
