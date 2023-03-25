package dev.ridill.mym.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

fun Long?.orZero(): Long = this ?: Long.Zero

inline fun Float.ifNaN(value: () -> Float): Float = if (isNaN()) value() else this

fun <T> Flow<T>.asStateFlow(
    scope: CoroutineScope,
    initialValue: T,
    stopTimeoutMillis: Long = 5_000L
): StateFlow<T> = this.stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
    initialValue = initialValue
)