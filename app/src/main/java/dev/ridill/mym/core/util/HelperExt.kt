package dev.ridill.mym.core.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import dev.ridill.mym.core.domain.model.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.io.NotActiveException

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

fun Context.launchUrl(url: String, onError: (UiText?) -> Unit) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    } catch (e: NotActiveException) {
        onError(e.message?.let { UiText.DynamicText(it) })
    } catch (t: Throwable) {
        onError(t.message?.let { UiText.DynamicText(it) })
    }
}

fun PermissionStatus.isPermanentlyDenied(): Boolean =
    !isGranted && !shouldShowRationale