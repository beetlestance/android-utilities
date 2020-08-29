package com.beetlestance.timer

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PeriodicTimer(
    private val initialDelayInMillis: Long,
    private val periodInMillis: Long,
    private val stopTimeInMillis: Long = 0,
    private val coroutineScope: CoroutineScope,
    private val action: (time: Long, countDownTimer: PeriodicTimer) -> Unit
) {

    private var currentExecutionCounter: Long = 0
    private var countDownTickerJob: Job? = null

    @ExperimentalCoroutinesApi
    private fun startNewCountdownJob() {
        val tickerFlow = tickerFlow(
            period = periodInMillis,
            initialDelay = initialDelayInMillis
        ).onEach {
            onTick()
        }

        countDownTickerJob = tickerFlow.launchIn(coroutineScope)
    }

    @ExperimentalCoroutinesApi
    @Synchronized
    fun start() {
        cancel()
        startNewCountdownJob()
        countDownTickerJob?.start()
    }

    /**
     * Cancels the timer without any callback
     */
    fun cancel() {
        cancelTimer()
    }

    private fun cancelTimer() {
        if (countDownTickerJob?.isActive == true) {
            countDownTickerJob?.cancel()
        }
        currentExecutionCounter = 0
    }

    /**
     * Cancels the timer with callback of execution time 0
     */
    fun reset() {
        cancelTimer()
        action(0, this)
    }

    private suspend fun onTick() {
        val executionTime = if (currentExecutionCounter == 0L) {
            initialDelayInMillis
        } else {
            periodInMillis * currentExecutionCounter
        }

        if (stopTimeInMillis in 1 until executionTime) {
            cancel()
            return
        }

        currentExecutionCounter += 1

        withContext(Dispatchers.Main) {
            action(executionTime, this@PeriodicTimer)
        }
    }

}