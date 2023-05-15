package dev.ridill.mym.core.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import dev.ridill.mym.R
import dev.ridill.mym.core.ui.theme.SpacingMedium
import dev.ridill.mym.core.ui.theme.SpacingSmall

@Composable
fun ConfirmationDialog(
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        title = { Text(stringResource(titleRes)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        text = { Text(stringResource(messageRes)) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        modifier = modifier
    )
}

@Composable
fun PermissionRationaleDialog(
    @StringRes rationalMessage: Int,
    icon: ImageVector,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    permissionGranted: Boolean = false
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IconContainerSize)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(PermissionIconSize)
                    )
                }
                VerticalSpacer(spacing = SpacingSmall)
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append(stringResource(R.string.app_name))
                        }
                        append(" ")
                        append(stringResource(rationalMessage))
                    },
                    modifier = Modifier
                        .padding(horizontal = SpacingMedium, vertical = SpacingSmall)
                )
                DialogButtons(
                    onDismiss = if (permissionGranted) null else onDismiss,
                    onConfirm = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DialogButtons(
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .padding(vertical = SpacingSmall, horizontal = SpacingMedium),
        mainAxisAlignment = FlowMainAxisAlignment.End,
        crossAxisAlignment = FlowCrossAxisAlignment.Center
    ) {
        onDismiss?.let { dismiss ->
            TextButton(onClick = { dismiss() }) {
                Text(stringResource(R.string.action_cancel))
            }
        }
        TextButton(onClick = onConfirm) {
            Text(stringResource(R.string.action_agree))
        }
    }
}

private val IconContainerSize = 120.dp
private val PermissionIconSize = IconContainerSize / 3