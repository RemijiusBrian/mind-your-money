package dev.ridill.mym.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun OnLifecycleEventEffect(
    lifecycleEvent: Lifecycle.Event,
    key: Any? = null,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    actionOnEvent: () -> Unit
) {
    DisposableEffect(lifecycleOwner, key) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == lifecycleEvent) actionOnEvent()
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun OnLifecycleStartEffect(
    key: Any? = null,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onStart: () -> Unit
) = OnLifecycleEventEffect(
    lifecycleEvent = Lifecycle.Event.ON_START,
    key = key,
    lifecycleOwner = lifecycleOwner,
    actionOnEvent = onStart
)