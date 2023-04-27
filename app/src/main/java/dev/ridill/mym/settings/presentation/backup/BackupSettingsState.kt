package dev.ridill.mym.settings.presentation.backup

import dev.ridill.mym.core.util.Zero

data class BackupSettingsState(
    val signedInAccountMail: String?,
    val isGoogleAccountSignedIn: Boolean,
    val isBackupInProgress: Boolean,
    val backupInterval: Int,
    val showRestorationWarning: Boolean
) {
    companion object {
        val INITIAL = BackupSettingsState(
            signedInAccountMail = null,
            isGoogleAccountSignedIn = false,
            isBackupInProgress = false,
            backupInterval = Int.Zero,
            showRestorationWarning = false
        )
    }
}