package dev.ridill.mym.settings.presentation.settings

import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.util.Zero

data class SettingsState(
    val appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val monthlyLimit: Long = Long.Zero,
    val loggedInUserEmail: String? = null,
    val showThemeSelection: Boolean = false,
    val showMonthlyLimitInput: Boolean = false,
    val showAutoAddExpenseDescription: Boolean = false,
    val isBackupInProgress: Boolean = false
) {
    companion object {
        val INITIAL = SettingsState()
    }
}