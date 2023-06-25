package dev.ridill.mym.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FadeVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    label: String = "FadeVisibility",
    enterAnimationSpec: FiniteAnimationSpec<Float> = DefaultFadeAnimationSpec,
    exitAnimationSpec: FiniteAnimationSpec<Float> = DefaultFadeAnimationSpec,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) = AnimatedVisibility(
    visible = visible,
    modifier = modifier,
    enter = fadeIn(enterAnimationSpec),
    exit = fadeOut(exitAnimationSpec),
    label = label,
    content = content
)

val DefaultFadeAnimationSpec: FiniteAnimationSpec<Float>
    get() = tween()