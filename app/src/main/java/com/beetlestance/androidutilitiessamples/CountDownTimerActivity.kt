package com.beetlestance.androidutilitiessamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.beetlestance.androidutilitiessamples.databinding.ActivityCountdownTimerBinding
import com.beetlestance.countdown_timer.CountDownTimer

class CountDownTimerActivity : AppCompatActivity() {

    lateinit var binding: ActivityCountdownTimerBinding
    private val timerToRun: Long = 10000

    private val countDownTimer = CountDownTimer(
        initialDelayInMillis = 0,
        periodInMillis = 1000,
        coroutineScope = lifecycleScope
    ) { time, countDownTimer ->
        if (time > timerToRun) {
            countDownTimer.cancel()
        } else {
            binding.activityCountdownTimerDisplay.text = (timerToRun - time).toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountdownTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityCountdownTimerCancel.setOnClickListener {
            countDownTimer.cancel()
        }

        binding.activityCountdownTimerReset.setOnClickListener {
            countDownTimer.reset()
        }

        binding.activityCountdownTimerStart.setOnClickListener {
            countDownTimer.start()
        }
    }
}