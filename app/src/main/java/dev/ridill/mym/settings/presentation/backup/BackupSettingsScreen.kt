package dev.ridill.mym.settings.presentation.backup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.ridill.mym.R
import dev.ridill.mym.core.navigation.screenSpecs.BackupSettingsScreenSpec
import dev.ridill.mym.core.ui.components.BackArrowButton
import dev.ridill.mym.core.ui.components.HorizontalSpacer
import dev.ridill.mym.core.ui.components.MYMScaffold
import dev.ridill.mym.core.ui.components.OnLifecycleStartEffect
import dev.ridill.mym.core.ui.components.SnackbarController
import dev.ridill.mym.core.ui.theme.ContentAlpha
import dev.ridill.mym.core.ui.theme.SpacingLarge
import dev.ridill.mym.core.ui.theme.SpacingMedium
import dev.ridill.mym.settings.presentation.components.BasicPreference
import dev.ridill.mym.settings.presentation.components.SectionTitle

@Composable
fun BackupSettingsScreenContent(
    snackbarController: SnackbarController,
    state: BackupSettingsState,
    actions: BackupSettingsActions,
    navigateUp: () -> Unit
) {
    OnLifecycleStartEffect(onStart = actions::getSignedInAccountDetails)

    MYMScaffold(
        snackbarController = snackbarController,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(BackupSettingsScreenSpec.label)) },
                navigationIcon = { BackArrowButton(onClick = navigateUp) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SpacingMedium)
        ) {
            Text(
                text = stringResource(R.string.backup_message),
                color = MaterialTheme.colorScheme.onSurface
                    .copy(alpha = ContentAlpha.PERCENT_60),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = SpacingLarge)
            )

            SectionTitle(title = R.string.pref_title_backup_account)
            BasicPreference(
                title = R.string.pref_google_account,
                summary = state.signedInAccountMail,
                onClick = actions::onGoogleAccountClick
            )

            AnimatedVisibility(visible = state.isGoogleAccountSignedIn) {
                BackupNow(
                    onBackupNowClick = actions::onBackupNowClick,
                    isBackupInProgress = state.isBackupInProgress
                )
            }

            Button(onClick = actions::onRestorationWarningConfirm) {
                Text(text = stringResource(R.string.restore_last_backup))
            }
        }
    }
}

@Composable
private fun BackupNow(
    onBackupNowClick: () -> Unit,
    isBackupInProgress: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onBackupNowClick,
            enabled = !isBackupInProgress
        ) {
            Text(stringResource(R.string.backup_now))
        }
        HorizontalSpacer(spacing = SpacingMedium)
        AnimatedVisibility(visible = isBackupInProgress) {
            LinearProgressIndicator()
        }
    }
}