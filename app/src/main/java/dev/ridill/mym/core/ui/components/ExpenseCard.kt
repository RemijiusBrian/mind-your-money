package dev.ridill.mym.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import dev.ridill.mym.core.ui.theme.SpacingMedium
import dev.ridill.mym.core.ui.theme.SpacingSmall
import dev.ridill.mym.core.ui.theme.SpacingXSmall
import dev.ridill.mym.core.util.One
import dev.ridill.mym.core.util.onColor
import dev.ridill.mym.expenses.domain.model.Tag

@Composable
fun ExpenseCard(
    note: String,
    date: String,
    amount: String,
    modifier: Modifier = Modifier,
    tag: Tag? = null,
    onClick: (() -> Unit)? = null,
    billExpense: Boolean = false,
    colors: CardColors = CardDefaults.cardColors(),
    shape: Shape = CardDefaults.shape
) {
    Card(
        modifier = Modifier
            .clip(shape)
            .then(
                if (billExpense || onClick == null) Modifier
                else Modifier.clickable { onClick() }
            )
            .then(modifier),
        colors = colors,
        shape = shape
    ) {
        ExpenseCardLayout(
            note = note,
            date = date,
            amount = amount,
            tag = tag
        )
    }
}

@Composable
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
}

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
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}