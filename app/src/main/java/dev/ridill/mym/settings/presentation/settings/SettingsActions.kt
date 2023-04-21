package dev.ridill.mym.settings.presentation.settings

import dev.ridill.mym.core.domain.model.AppTheme

interface SettingsActions {
    fun getSignedInAccountDetails()

    fun onThemePreferenceClick()
    fun onAppThemeSelectionDismiss()
    fun onAppThemeSelectionConfirm(theme: AppTheme)

    fun onMonthlyLimitPreferenceClick()
    fun onMonthlyLimitInputDismiss()
    fun onMonthlyLimitInputConfirm(amount: String)

    fun onAutoAddExpenseClick()
    fun onAutoAddExpenseDismiss()
    fun onAutoAddExpenseConfirm()

    fun onGoogleAccountSelectionClick()
    fun onPerformBackupClick()
}