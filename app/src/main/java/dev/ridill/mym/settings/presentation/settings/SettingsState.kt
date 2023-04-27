package dev.ridill.mym.settings.presentation.settings

import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.util.Zero

data class SettingsState(
    val appTheme: AppTheme,
    val monthlyLimit: Long,
    val showThemeSelection: Boolean,
    val showMonthlyLimitInput: Boolean,
    val showAutoAddExpenseDescription: Boolean
) {
    companion object {
        val INITIAL = SettingsState(
            appTheme = AppTheme.DYNAMIC,
            monthlyLimit = Long.Zero,
            showThemeSelection = false,
            showMonthlyLimitInput = false,
            showAutoAddExpenseDescription = false
        )
    }
}