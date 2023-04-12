package dev.ridill.mym.settings.presentation.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import dev.ridill.mym.core.ui.components.*
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
    MYMScaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(SettingsScreenSpec.label))
                },
                navigationIcon = { BackArrowButton(onClick = navigateUp) }
            )
        },
        snackbarController = snackbarController,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // General Section
            SectionTitle(title = R.string.pref_title_general)
            BasicPreference(
                title = R.string.pref_theme,
                summary = stringResource(state.appTheme.label),
                icon = Icons.Default.BrightnessMedium,
                onClick = actions::onThemePreferenceClick
            )
            BasicPreference(
                title = R.string.pref_notifications,
                icon = Icons.Outlined.Notifications,
                onClick = navigateToNotificationSettings
            )

            // Expense Section
            SectionTitle(title = R.string.pref_title_expense)
            BasicPreference(
                title = R.string.pref_monthly_limit,
                summary = state.monthlyLimit,
                onClick = actions::onMonthlyLimitPreferenceClick
            )
            BasicPreference(
                title = R.string.pref_auto_add_expense,
                summary = stringResource(R.string.pref_summary_auto_add_expense),
                onClick = actions::onAutoAddExpenseClick
            )

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
            SectionTitle(title = R.string.links)
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

            // Info Section
            SectionTitle(title = R.string.pref_title_info)
            BasicPreference(
                title = R.string.pref_app_version,
                summary = BuildConfig.VERSION_NAME,
                icon = Icons.Default.Info
            )
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
                previousLimit = state.monthlyLimit,
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
                onConfirm = actions::onAutoAddExpenseConfirm
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
            TextButton(onClick = { onConfirm(input) }) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        icon = {
//            Icon(
//                painter = painterResource(R.drawable.ic_piggy_bank),
//                contentDescription = null
//            )
        },
        title = {
            Text(stringResource(R.string.enter_monthly_limit))
        },
        text = {
            Column {
//                Text(text = stringResource(R.string))
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