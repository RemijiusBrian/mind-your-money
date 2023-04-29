package dev.ridill.mym.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.theme.SpacingLarge
import dev.ridill.mym.core.ui.theme.SpacingMedium
import dev.ridill.mym.core.ui.theme.SpacingSmall
import dev.ridill.mym.expenses.domain.model.TagColors

@Composable
fun NewTagSheetContent(
    name: () -> String,
    onTagNameChange: (String) -> Unit,
    colorCode: Int?,
    onTagColorSelect: (Color) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    inputFocusRequester: FocusRequester = remember { FocusRequester() }
) {
    Column(
        modifier = Modifier
            .padding(windowInsets.asPaddingValues())
            .padding(horizontal = SpacingLarge)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(SpacingMedium)
    ) {
        VerticalSpacer(SpacingLarge)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TitleText(labelRes = R.string.new_tag)
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.content_dismiss)
                )
            }
        }
        OutlinedTextField(
            value = name(),
            onValueChange = onTagNameChange,
            modifier = Modifier
                .focusRequester(inputFocusRequester)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            label = { Text(stringResource(R.string.name)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SpacingSmall)
        ) {
            items(items = TagColors) { color ->
                ColorSelector(
                    color = color,
                    selected = colorCode == color.toArgb(),
                    onClick = { onTagColorSelect(color) }
                )
            }
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.action_confirm))
        }
    }
}

@Composable
private fun ColorSelector(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectorSize: Dp = ColorSelectorSize,
    borderWidth: Dp = ColorSelectorBorderWidth,
    borderColor: Color = LocalContentColor.current
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(selectorSize)
            .clickable { onClick() }
            .drawBehind {
                val selectorBorderWidthPx = borderWidth.toPx()
                drawCircle(
                    color = color,
                    radius = (size.minDimension / 2f) - (selectorBorderWidthPx)
                )

                if (selected) {
                    drawCircle(
                        color = borderColor,
                        style = Stroke(selectorBorderWidthPx)
                    )
                }
            }
    )
}

private val ColorSelectorSize = 32.dp
private val ColorSelectorBorderWidth = 4.dp