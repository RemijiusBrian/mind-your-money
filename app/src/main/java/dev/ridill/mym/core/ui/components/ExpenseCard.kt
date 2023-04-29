package dev.ridill.mym.core.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.ridill.mym.core.ui.theme.ContentAlpha
import dev.ridill.mym.core.ui.theme.SpacingSmall
import dev.ridill.mym.core.ui.theme.SpacingXSmall
import dev.ridill.mym.core.util.onColor
import dev.ridill.mym.expenses.domain.model.Tag

@Composable
fun ExpenseCard(
    onClick: () -> Unit,
    note: String,
    date: String,
    amount: String,
    modifier: Modifier = Modifier,
    tag: Tag? = null,
    colors: ListItemColors = ListItemDefaults.colors(),
    shape: Shape = CardDefaults.shape
) {
    Card(
        onClick = onClick,
        shape = shape
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = note,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall
                    )
                    tag?.let {
                        Spacer(Modifier.width(SpacingSmall))
                        TagIndicator(
                            name = it.name,
                            color = it.color
                        )
                    }
                }
            },
            colors = colors,
            trailingContent = {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .widthIn(max = ExpenseAmountMaxWidth)
                )
            },
            modifier = modifier
        )
    }
}

private val ExpenseAmountMaxWidth = 240.dp

@Composable
fun SelectableExpenseCard(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    note: String,
    date: String,
    amount: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    tag: Tag? = null,
    colors: ListItemColors = ListItemDefaults.colors(
        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = ContentAlpha.PERCENT_16)
        else MaterialTheme.colorScheme.surface
    ),
    shape: Shape = CardDefaults.shape
) {
    Card(
        shape = shape,
        modifier = Modifier
            .clip(shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = note,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall
                    )
                    tag?.let {
                        Spacer(Modifier.width(SpacingSmall))
                        TagIndicator(
                            name = it.name,
                            color = it.color
                        )
                    }
                }
            },
            colors = colors,
            trailingContent = {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .widthIn(max = 120.dp)
                )
            },
            modifier = modifier
        )
    }
}

/*@Composable
fun ExpenseCardLayout(
    note: String,
    date: String,
    amount: String,
    tag: Tag?,
) {
    Row(
        modifier = Modifier
            .padding(SpacingMedium)
    ) {
        Column(
            modifier = Modifier
                .weight(Float.One)
        ) {
            Text(
                text = note,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.width(SpacingSmall))
                tag?.let {
                    TagIndicator(
                        name = it.name,
                        color = it.color
                    )
                }
            }
        }
        Spacer(Modifier.width(SpacingMedium))
        Text(
            text = amount,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}*/

@Composable
private fun TagIndicator(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color,
        shape = FilterChipDefaults.shape,
        modifier = modifier,
        contentColor = color.onColor()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(
                    horizontal = SpacingSmall,
                    vertical = SpacingXSmall
                )
        ) {
            Text(
                text = name,
                modifier = Modifier
                    .padding(horizontal = SpacingSmall),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}