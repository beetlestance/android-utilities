package com.beetlestance.timer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import kotlin.concurrent.schedule

@ExperimentalCoroutinesApi
fun tickerFlow(
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