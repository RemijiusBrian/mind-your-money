package dev.ridill.mym.settings.presentation.backup

interface BackupSettingsActions {
    fun getSignedInAccountDetails()
    fun onGoogleAccountClick()
    fun onBackupNowClick()
    fun onRestoreBackupClick()
    fun onRestorationWarningDismiss()
    fun onRestorationWarningConfirm()
}