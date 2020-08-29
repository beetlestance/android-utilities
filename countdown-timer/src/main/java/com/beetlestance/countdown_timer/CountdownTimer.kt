package com.beetlestance.countdown_timer

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import kotlin.concurrent.schedule

class CountDownTimer(
    private val initialDelayInMillis: Long,
    private val periodInMillis: Long,
    private val coroutineScope: CoroutineScope,
    private val action: (time: Long, countDownTimer: CountDownTimer) -> Unit
) {

    private var currentExecutionCounter: Long = 0
    private var countDownTickerJob: Job? = null

    @ExperimentalCoroutinesApi
    @Synchronized
    fun start() {
        cancel()
        countDownTickerJob = tickerFlow(
            period = periodInMillis,
            initialDelay = initialDelayInMillis
        ).onEach {
            onTick()
        }.launchIn(coroutineScope)

        countDownTickerJob?.start()
    }


    /**
     * Cancel the timer without setting the current execution time to 0
     */
    fun cancel() {
        cancelTimer()
        currentExecutionCounter = 0
    }

    private fun cancelTimer() {
        if (countDownTickerJob?.isActive == true) {
            countDownTickerJob?.cancel()
        }
    }

    fun reset() {
        cancelTimer()
        currentExecutionCounter = 0
        action(0, this)
    }

    private suspend fun onTick() {
        val executionTime = if (currentExecutionCounter == 0L) {
            initialDelayInMillis
        } else {
            periodInMillis * currentExecutionCounter
        }

        currentExecutionCounter += 1

        withContext(Dispatchers.Main) {
            action(executionTime, this@CountDownTimer)
        }
    }

    @ExperimentalCoroutinesApi
    private fun tickerFlow(
        period: Long,
        initialDelay: Long = period
    ): Flow<Unit> = callbackFlow {
        require(period > 0)
        require(initialDelay > -1)

        val timer = Timer()
        timer.schedule(initialDelay, period) {
            offer(Unit)
        }

        awaitClose { timer.cancel() }
    }
}