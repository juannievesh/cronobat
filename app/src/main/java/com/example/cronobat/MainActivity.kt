package com.example.cronobat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupListeners()
    }
    
    private fun setupUI() {
        // Set initial time display
        updateTimeDisplay(0)
        
        // Set initial seekbar values
        binding.seekBarTimer.max = 3600 // 1 hour max
        binding.seekBarTimer.progress = 0
        
        // Set initial music type
        updateMusicButton()
    }
    
    private fun setupListeners() {
        // Timer controls
        binding.btnStartTimer.setOnClickListener {
            if (!isTimerRunning) {
                startTimer()
            } else {
                pauseTimer()
            }
        }
        
        binding.btnResetTimer.setOnClickListener {
            resetTimer()
        }
        
        // Stopwatch controls
        binding.btnStartStopwatch.setOnClickListener {
            if (!isStopwatchRunning) {
                startStopwatch()
            } else {
                pauseStopwatch()
            }
        }
        
        binding.btnResetStopwatch.setOnClickListener {
            resetStopwatch()
        }
        
        // Music controls
        binding.btnToggleMusic.setOnClickListener {
            toggleBackgroundMusic()
        }
        
        binding.btnChangeMusic.setOnClickListener {
            changeBackgroundMusic()
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
        
        // Mode switching
        binding.btnSwitchMode.setOnClickListener {
            switchMode()
        }
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
            }
            
            override fun onFinish() {
                isTimerRunning = false
                currentTimeInSeconds = 0
                updateTimeDisplay(0)
                updateTimerButton()
                Toast.makeText(this@MainActivity, "¡Tiempo completado!", Toast.LENGTH_LONG).show()
            }
        }.start()
        
        updateTimerButton()
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
                    stopwatchHandler.postDelayed(this, 1000)
                }
            }
        }
        
        stopwatchHandler.post(stopwatchRunnable!!)
        updateStopwatchButton()
    }
    
    private fun pauseStopwatch() {
        isStopwatchRunning = false
        stopwatchRunnable?.let { stopwatchHandler.removeCallbacks(it) }
        updateStopwatchButton()
    }
    
    private fun resetStopwatch() {
        isStopwatchRunning = false
        stopwatchRunnable?.let { stopwatchHandler.removeCallbacks(it) }
        currentTimeInSeconds = 0
        updateTimeDisplay(0)
        updateStopwatchButton()
    }
    
    private fun toggleBackgroundMusic() {
        isMusicPlaying = !isMusicPlaying
        updateMusicButton()
        
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
        
        updateMusicButton()
        Toast.makeText(this, "Música cambiada a: $currentMusicType", Toast.LENGTH_SHORT).show()
        
        // TODO: Implement music change
    }
    
    private fun switchMode() {
        // Reset both timer and stopwatch when switching modes
        resetTimer()
        resetStopwatch()
        
        // Toggle between timer and stopwatch mode
        if (binding.timerLayout.visibility == View.VISIBLE) {
            binding.timerLayout.visibility = View.GONE
            binding.stopwatchLayout.visibility = View.VISIBLE
            binding.btnSwitchMode.text = "Modo Temporizador"
        } else {
            binding.timerLayout.visibility = View.VISIBLE
            binding.stopwatchLayout.visibility = View.GONE
            binding.btnSwitchMode.text = "Modo Cronómetro"
        }
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
        
        binding.tvTimeDisplay.text = timeString
    }
    
    private fun updateTimerButton() {
        binding.btnStartTimer.text = if (isTimerRunning) "Pausar" else "Iniciar"
    }
    
    private fun updateStopwatchButton() {
        binding.btnStartStopwatch.text = if (isStopwatchRunning) "Pausar" else "Iniciar"
    }
    
    private fun updateMusicButton() {
        binding.btnToggleMusic.text = if (isMusicPlaying) "Pausar Música" else "Iniciar Música"
        binding.btnChangeMusic.text = "Cambiar: $currentMusicType"
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        stopwatchRunnable?.let { stopwatchHandler.removeCallbacks(it) }
    }
}
