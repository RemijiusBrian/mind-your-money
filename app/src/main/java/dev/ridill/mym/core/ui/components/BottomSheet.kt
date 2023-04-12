package dev.ridill.mym.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
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
    windowInsets: WindowInsets = WindowInsets.navigationBars
) {
    Column(
        modifier = Modifier
            .padding(windowInsets.asPaddingValues())
            .padding(horizontal = SpacingLarge)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(SpacingMedium)
    ) {
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
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            label = { Text(stringResource(R.string.name)) }
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