package dev.ridill.mym.core.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun <T> VerticalNumberSpinner(
    targetState: T,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable AnimatedVisibilityScope.(targetState: T) -> Unit,
) where T : Number, T : Comparable<T> {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            verticalSpinner { this.targetState > this.initialState }
        },
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content
    )
}

fun <T> AnimatedContentTransitionScope<T>.verticalSpinner(
    slideUpFromBottom: () -> Boolean = { initialState isTransitioningTo targetState },
): ContentTransform = if (slideUpFromBottom()) {
    (slideInVertically { it / 2 } + fadeIn()) togetherWith slideOutVertically { -it / 2 } + fadeOut()
} else {
    slideInVertically { -it / 2 } + fadeIn() togetherWith
            slideOutVertically { it / 2 } + fadeOut()
} using SizeTransform(clip = false)