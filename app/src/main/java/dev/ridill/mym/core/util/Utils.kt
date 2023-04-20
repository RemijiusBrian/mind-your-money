package dev.ridill.mym.core.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import timber.log.Timber

inline fun <T> tryOrNull(block: () -> T): T? = try {
    block()
} catch (t: Throwable) {
    logE(t) { "tryOrNull Error" }
    null
}

fun Color.onColor(
    onBrightColor: Color = Color.Black,
    onDarkColor: Color = Color.White
): Color = if (luminance() > 0.25f) onBrightColor else onDarkColor

inline fun logD(vararg args: Any = emptyArray(), statement: () -> Any) =
    Timber.d("AppDebug: ${statement()}", *args)

inline fun logE(throwable: Throwable, vararg args: Any = emptyArray(), statement: () -> Any) =
    Timber.e(throwable, "AppDebug: ${statement()}", *args)

inline fun logI(vararg args: Any = emptyArray(), statement: () -> Any) =
    Timber.i("AppDebug: ${statement()}", *args)