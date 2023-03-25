package dev.ridill.mym.core.ui.components

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun <T> VerticalSpinner(
    targetState: T,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable AnimatedVisibilityScope.(targetState: T) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = { verticalSpinner() },
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content
    )
}

fun <T> AnimatedContentScope<T>.verticalSpinner(
    slideUpFromBottom: () -> Boolean = { initialState isTransitioningTo targetState },
): ContentTransform = if (slideUpFromBottom()) {
    slideInVertically { height -> height } + fadeIn() with
            slideOutVertically { height -> -height } + fadeOut()
} else {
    slideInVertically { height -> -height } + fadeIn() with
            slideOutVertically { height -> height } + fadeOut()
} using SizeTransform(clip = false)