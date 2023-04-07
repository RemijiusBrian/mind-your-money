package dev.ridill.mym.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import dev.ridill.mym.core.ui.theme.ContentAlpha
import dev.ridill.mym.core.ui.theme.SpacingSmall

@Composable
fun MinWidthTextField(
    valueProvider: () -> String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val showPlaceholder by remember {
        derivedStateOf { valueProvider().isEmpty() }
    }
    BasicTextField(
        value = valueProvider(),
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = textStyle
            .copy(color = contentColor),
        cursorBrush = SolidColor(contentColor),
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        enabled = enabled,
        readOnly = readOnly,
        decorationBox = { innerTextField ->
            ProvideTextStyle(value = textStyle) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                ) {
                    leadingIcon?.let { icon ->
                        icon()
                        HorizontalSpacer(spacing = SpacingSmall)
                    }
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        placeholder?.let {
                            this@Row.AnimatedVisibility(showPlaceholder) {
                                Text(
                                    text = it,
                                    color = contentColor.copy(alpha = ContentAlpha.PERCENT_32)
                                )
                            }
                        }
                        innerTextField()
                    }
                    trailingIcon?.let { icon ->
                        HorizontalSpacer(spacing = SpacingSmall)
                        icon()
                    }
                }
            }
        }
    )
}