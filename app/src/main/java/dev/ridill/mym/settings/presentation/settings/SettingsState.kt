package dev.ridill.mym.settings.presentation.settings

import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.util.Zero

data class SettingsState(
    val appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val materialYouThemeEnabled: Boolean = false,
    val monthlyLimit: Long = Long.Zero,
    val showThemeSelection: Boolean = false,
    val showMonthlyLimitInput: Boolean = false,
    val showAutoAddExpenseDescription: Boolean = false,
    val backupAccount: String? = null
) {
    companion object {
        val INITIAL = SettingsState()
    }
}