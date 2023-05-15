package dev.ridill.mym.core.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.content.ContextCompat
import timber.log.Timber

inline fun <T> tryOrNull(block: () -> T): T? = try {
    block()
} catch (t: Throwable) {
    t.printStackTrace()
    null
}

fun Color.onColor(
    onBrightColor: Color = Color.Black,
    onDarkColor: Color = Color.White
): Color = if (luminance() > 0.25f) onBrightColor else onDarkColor

fun isBuildAtLeastVersionCodeS(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun isPermissionGranted(context: Context, permission: String): Boolean =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

inline fun logD(vararg args: Any = emptyArray(), statement: () -> Any) =
    Timber.d("AppDebug: ${statement()}", *args)

inline fun logE(throwable: Throwable, vararg args: Any = emptyArray(), statement: () -> Any) =
    Timber.e(throwable, "AppDebug: ${statement()}", *args)

inline fun logI(vararg args: Any = emptyArray(), statement: () -> Any) =
    Timber.i("AppDebug: ${statement()}", *args)