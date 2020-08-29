package com.beetlestance.timer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import kotlin.concurrent.schedule

@ExperimentalCoroutinesApi
fun tickerFlow(
    periodInMillis: Long,
    initialDelayInMillis: Long = periodInMillis
): Flow<Unit> = callbackFlow {
    require(periodInMillis > 0)
    require(initialDelayInMillis > -1)

    val timer = Timer()
    timer.schedule(initialDelayInMillis, periodInMillis) {
        offer(Unit)
    }

    awaitClose { timer.cancel() }
}