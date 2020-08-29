package com.beetlestance.androidutilitiessamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.beetlestance.androidutilitiessamples.databinding.ActivityCountdownTimerBinding
import com.beetlestance.timer.PeriodicTimer

class CountDownTimerActivity : AppCompatActivity() {

    lateinit var binding: ActivityCountdownTimerBinding
    private val stopTimeInMillis = 45000L

    private val periodicTimer = PeriodicTimer(
        initialDelayInMillis = 0,
        periodInMillis = 1000,
        stopTimeInMillis = 45000,
        coroutineScope = lifecycleScope
    ) { time, _ ->
        binding.activityCountdownTimerDisplay.text = (stopTimeInMillis - time).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountdownTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityCountdownTimerCancel.setOnClickListener {
            periodicTimer.cancel()
        }

        binding.activityCountdownTimerReset.setOnClickListener {
            periodicTimer.reset()
        }

        binding.activityCountdownTimerStart.setOnClickListener {
            periodicTimer.start()
        }
    }
}