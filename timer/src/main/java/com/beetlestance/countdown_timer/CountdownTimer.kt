package com.beetlestance.countdown_timer

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class CountDownTimerLifecycleScoped(
    private val coroutineScope: CoroutineScope,
    private val countDownTimeInMillis: Long,
    private val intervalInMillis: Long
) {

    private var mStopTimeInFuture: Long = 0

    private var countDownTickerJob: Job? = null

    /**
     * Callback fired on regular interval.
     * @see millisUntilFinished The amount of time until finished.
     */
    private val _millisUntilFinished: MutableLiveData<Long> = MutableLiveData()
    val millisUntilFinished: LiveData<Long> = _millisUntilFinished


    @Synchronized
    fun start() {
        cancelTimer()
        if (countDownTimeInMillis <= 0) {
            _millisUntilFinished.value = 0L
        } else {
            mStopTimeInFuture = SystemClock.elapsedRealtime() + countDownTimeInMillis
            countDownTickerJob = coroutineScope.launch(Dispatchers.Default) {
                startCountDownTicker()
            }
        }

    }

    @Synchronized
    fun reset() {
        cancelTimer()
        coroutineScope.launch(Dispatchers.IO) {
            _millisUntilFinished.postValue(0L)
        }
    }

    private suspend fun startCountDownTicker() {
        val millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()
        if (millisLeft <= 0) {
            _millisUntilFinished.postValue(0L)
        } else {
            val lastTickStart = SystemClock.elapsedRealtime()
            _millisUntilFinished.postValue(millisLeft)

            // take into account user's onTick taking time to execute
            val lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart
            var mDelay: Long
            if (millisLeft < intervalInMillis) {
                // just delay until done
                mDelay = millisLeft - lastTickDuration

                // special case: user's onTick took more than interval to
                // complete, trigger onFinish without delay
                if (mDelay < 0) mDelay = 0
            } else {
                mDelay = intervalInMillis - lastTickDuration

                // special case: user's onTick took more than interval to
                // complete, skip to next interval
                while (mDelay < 0) mDelay += intervalInMillis
            }
            delay(mDelay)
            startCountDownTicker()
        }
    }

    private fun cancelTimer() {
        if (countDownTickerJob?.isActive == true) {
            countDownTickerJob?.cancel()
        }
    }
}