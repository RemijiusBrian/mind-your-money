package dev.ridill.mym.settings.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import dev.ridill.mym.core.ui.components.VerticalSpacer
import dev.ridill.mym.core.ui.theme.ContentAlpha
import dev.ridill.mym.core.ui.theme.SpacingLarge
import dev.ridill.mym.core.ui.theme.SpacingMedium
import dev.ridill.mym.core.ui.theme.SpacingSmall

@Composable
fun SectionTitle(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text = stringResource(title),
        style = textStyle,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .padding(horizontal = SpacingLarge)
            .padding(top = SpacingMedium)
    )
}

@Composable
fun BasicPreference(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    summary: String? = null,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    titleStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    summaryStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    contentColor: Color = LocalContentColor.current
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(SpacingLarge)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(Modifier.width(SpacingMedium))
        }
        Column {
            Text(
                text = stringResource(title),
                style = titleStyle
            )
            if (!summary.isNullOrBlank()) {
                VerticalSpacer(spacing = SpacingSmall)
                Text(
                    text = summary,
                    style = summaryStyle,
                    color = contentColor.copy(alpha = ContentAlpha.PERCENT_60)
                )
            }
        }
    }
}

@Composable
fun BasicPreference(
    @StringRes title: Int,
    secondaryContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    titleStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    secondaryContentStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    contentColor: Color = LocalContentColor.current
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(SpacingLarge)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(Modifier.width(SpacingMedium))
        }
        Column {
            Text(
                text = stringResource(title),
                style = titleStyle
            )
            VerticalSpacer(spacing = SpacingSmall)
            ProvideTextStyle(
                value = secondaryContentStyle
                    .copy(color = contentColor.copy(alpha = ContentAlpha.PERCENT_60)),
                content = secondaryContent
            )
        }
    }
}