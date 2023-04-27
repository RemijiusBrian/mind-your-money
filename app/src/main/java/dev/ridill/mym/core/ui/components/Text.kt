package dev.ridill.mym.core.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LabelText(
    @StringRes labelRes: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    fontWeight: FontWeight = FontWeight.Medium,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text = stringResource(labelRes),
        style = textStyle,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TitleText(
    @StringRes labelRes: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    fontWeight: FontWeight = FontWeight.SemiBold,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text = stringResource(labelRes),
        style = textStyle,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}