package dev.ridill.mym.settings.presentation.settings

import android.Manifest
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import dev.ridill.mym.BuildConfig
import dev.ridill.mym.R
import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.navigation.screenSpecs.SettingsScreenSpec
import dev.ridill.mym.core.ui.components.BackArrowButton
import dev.ridill.mym.core.ui.components.MYMScaffold
import dev.ridill.mym.core.ui.components.OnLifecycleStartEffect
import dev.ridill.mym.core.ui.components.PermissionRationaleDialog
import dev.ridill.mym.core.ui.components.RadioButtonWithLabel
import dev.ridill.mym.core.ui.components.SnackbarController
import dev.ridill.mym.core.ui.components.VerticalSpacer
import dev.ridill.mym.core.ui.theme.SpacingMedium
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.core.util.isBuildAtLeastVersionCodeS
import dev.ridill.mym.core.util.isPermissionGranted
import dev.ridill.mym.core.util.launchUrl
import dev.ridill.mym.settings.presentation.components.BasicPreference
import dev.ridill.mym.settings.presentation.components.SectionTitle

@Composable
fun SettingsScreenContent(
    snackbarController: SnackbarController,
    context: Context,
    state: SettingsState,
    actions: SettingsActions,
    navigateUp: () -> Unit,
    navigateToNotificationSettings: () -> Unit
) {
    var isSMSPermissionGranted by remember { mutableStateOf(false) }

    OnLifecycleStartEffect {
        isSMSPermissionGranted = isPermissionGranted(context, Manifest.permission.READ_SMS)
    }

    MYMScaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(SettingsScreenSpec.label)) },
                navigationIcon = { BackArrowButton(onClick = navigateUp) }
            )
        },
        snackbarController = snackbarController,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = paddingValues
        ) {
            // General Section
            item(key = "Section Title General") {
                SectionTitle(title = R.string.pref_title_general)
            }

            item(key = "Preference Theme") {
                BasicPreference(
                    title = R.string.pref_theme,
                    summary = stringResource(state.appTheme.label),
                    icon = Icons.Default.BrightnessMedium,
                    onClick = actions::onThemePreferenceClick
                )
            }

            if (isBuildAtLeastVersionCodeS()) {
                item(key = "Preference Material You") {
                    BasicPreference(
                        title = R.string.pref_material_you,
                        summary = stringResource(R.string.pref_summary_material_you),
                        icon = ImageVector.vectorResource(R.drawable.ic_color_palette),
                        onClick = { actions.toggleMaterialYou(!state.materialYouThemeEnabled) }
                    ) {
                        Switch(
                            checked = state.materialYouThemeEnabled,
                            onCheckedChange = actions::toggleMaterialYou
                        )
                    }
                }
            }

            item(key = "Preference Notifications") {
                BasicPreference(
                    title = R.string.pref_notifications,
                    icon = Icons.Outlined.Notifications,
                    onClick = navigateToNotificationSettings
                )
            }

            // Expense Section
            item(key = "Section Title Expense") {
                SectionTitle(title = R.string.pref_title_expense)
            }

            item(key = "Preference Monthly Limit") {
                BasicPreference(
                    title = R.string.pref_monthly_limit,
                    summary = if (state.monthlyLimit <= 0) stringResource(R.string.disabled)
                    else Formatter.currency(state.monthlyLimit),
                    onClick = actions::onMonthlyLimitPreferenceClick
                )
            }

            item(key = "Preference Auto Add Expenses") {
                BasicPreference(
                    title = R.string.pref_auto_add_expense,
                    summary = stringResource(R.string.pref_summary_auto_add_expense),
                    onClick = actions::onAutoAddExpenseClick
                )
            }

            // Backup Section
            /*SectionTitle(title = R.string.backup)
            BasicPreference(
                title = R.string.google_account,
                summary = state.backupAccount,
                icon = ImageVector.vectorResource(R.drawable.ic_google),
                onClick = actions::onGoogleAccountSelectionClick
            )
            BasicPreference(
                title = R.string.perform_data_backup,
                icon = Icons.Default.CloudUpload,
                onClick = actions::onPerformBackupClick
            )*/

            // Links Section
            item(key = "Section Title Links") {
                SectionTitle(title = R.string.links)
            }
            item(key = "Preference Source Code") {
                BasicPreference(
                    title = R.string.pref_source_code,
                    onClick = {
                        context.launchUrl(BuildConfig.REPO_URL) { uiText ->
                            uiText?.let {
                                snackbarController.showSnackbar(it.asString(context))
                            }
                        }
                    },
                    icon = ImageVector.vectorResource(R.drawable.ic_github)
                )
            }

            // Info Section
            item(key = "Section Title Info") {
                SectionTitle(title = R.string.pref_title_info)
            }
            item(key = "Preference App Version") {
                BasicPreference(
                    title = R.string.pref_app_version,
                    summary = BuildConfig.VERSION_NAME,
                    icon = Icons.Default.Info
                )
            }
        }

        if (state.showThemeSelection) {
            ThemeSelectionDialog(
                selectedTheme = state.appTheme,
                onDismiss = actions::onAppThemeSelectionDismiss,
                onConfirm = actions::onAppThemeSelectionConfirm
            )
        }

        if (state.showMonthlyLimitInput) {
            MonthlyLimitInputDialog(
                previousLimit = Formatter.currency(state.monthlyLimit),
                onDismiss = actions::onMonthlyLimitInputDismiss,
                onConfirm = actions::onMonthlyLimitInputConfirm
            )
        }

        if (state.showAutoAddExpenseDescription) {
            PermissionRationaleDialog(
                rationalMessage = R.string.permission_receive_sms_rationale,
                icon = Icons.Outlined.Message,
//                ImageVector.vectorResource(R.drawable.ic_message),
                onDismiss = actions::onAutoAddExpenseDismiss,
                onConfirm = actions::onAutoAddExpenseConfirm,
                permissionGranted = isSMSPermissionGranted
            )
        }
    }
}

@Composable
private fun ThemeSelectionDialog(
    selectedTheme: AppTheme,
    onDismiss: () -> Unit,
    onConfirm: (AppTheme) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text(text = stringResource(R.string.select_theme)) },
        text = {
            Column {
                AppTheme.values().forEach { theme ->
                    RadioButtonWithLabel(
                        label = theme.label,
                        selected = theme == selectedTheme,
                        onClick = { onConfirm(theme) },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.BrightnessMedium,
                contentDescription = null
            )
        }
    )
}

@Composable
private fun MonthlyLimitInputDialog(
    previousLimit: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onConfirm(input) }) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        title = { Text(stringResource(R.string.enter_monthly_limit)) },
        text = {
            Column {
                Text(text = stringResource(R.string.monthly_limit_input_message))
                VerticalSpacer(spacing = SpacingMedium)
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    placeholder = { Text(previousLimit) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }
    )
}