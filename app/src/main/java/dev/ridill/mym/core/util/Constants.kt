package dev.ridill.mym.core.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val Float.Companion.Zero: Float inline get() = 0f
val Float.Companion.One: Float inline get() = 1f

val Double.Companion.Zero: Double inline get() = 0.0
val Double.Companion.One: Double inline get() = 1.0

val Int.Companion.Zero: Int inline get() = 0

val Long.Companion.Zero: Long inline get() = 0L

val String.Companion.Empty: String inline get() = ""

val Dp.Companion.Zero: Dp inline get() = 0.dp