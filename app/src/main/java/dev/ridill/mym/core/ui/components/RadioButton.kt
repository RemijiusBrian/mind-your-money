package dev.ridill.mym.core.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import dev.ridill.mym.core.ui.theme.SpacingSmall

@Composable
fun RadioButtonWithLabel(
    @StringRes label: Int,
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .then(
                if (onClick != null) Modifier.clickable(
                    role = Role.RadioButton,
                    onClick = onClick
                ) else Modifier
            )
            .padding(SpacingSmall)
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        HorizontalSpacer(spacing = SpacingSmall)
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}