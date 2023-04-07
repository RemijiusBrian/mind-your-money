package dev.ridill.mym.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.theme.SpacingLarge
import dev.ridill.mym.core.ui.theme.SpacingSmall
import dev.ridill.mym.core.util.onColor
import dev.ridill.mym.expenses.domain.model.TagColors

@Composable
fun NewTagSheet(
    name: () -> String,
    onTagNameChange: (String) -> Unit,
    colorCode: Int?,
    onTagColorSelect: (Color) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(
                vertical = SpacingSmall,
                horizontal = SpacingLarge
            ),
        verticalArrangement = Arrangement.spacedBy(SpacingSmall)
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
//        Divider()
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
    borderColor: Color = color.onColor()
) {
    val borderWidthFraction by animateFloatAsState(targetValue = if (selected) 0.30f else 0.10f)
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(ColorSelectorSize)
            .clickable { onClick() }
            .drawBehind {
                drawCircle(color)
                drawCircle(
                    color = borderColor,
                    style = Stroke(ColorSelectorSize.toPx() * borderWidthFraction)
                )
            }
    )
}

private val ColorSelectorSize = 32.dp