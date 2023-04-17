package dev.ridill.mym.settings.presentation.settings

import android.content.IntentSender
import androidx.activity.result.ActivityResult
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.mym.R
import dev.ridill.mym.core.data.preferences.PreferencesManager
import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.domain.model.UiText
import dev.ridill.mym.core.util.asStateFlow
import dev.ridill.mym.settings.presentation.sign_in.GoogleAuthClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferencesManager: PreferencesManager,
    private val googleAuthClient: GoogleAuthClient
) : ViewModel(), SettingsActions {

    private val preferences = preferencesManager.preferences
    private val appTheme = preferences.map { it.theme }.distinctUntilChanged()
    private val monthlyLimit = preferences.map { it.monthlyLimit }.distinctUntilChanged()
    private val loggedInUserEmail = preferences.map { it.loggedInUserEmail }.distinctUntilChanged()

    private val showThemeSelection = savedStateHandle
        .getStateFlow(SHOW_THEME_SELECTION, false)
    private val showMonthlyLimitInput = savedStateHandle
        .getStateFlow(SHOW_MONTHLY_LIMIT_INPUT, false)

    private val showAutoAddExpenseDescription =
        savedStateHandle.getStateFlow(KEY_SHOW_AUTO_ADD_EXPENSE_DESC, false)

    private val eventsChannel = Channel<SettingsEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    val state = combineTuple(
        appTheme,
        monthlyLimit,
        loggedInUserEmail,
        showThemeSelection,
        showMonthlyLimitInput,
        showAutoAddExpenseDescription
    ).map { (
                appTheme,
                monthlyLimit,
                loggedInUserEmail,
                showThemeSelection,
                showMonthlyLimitInput,
                showAutoAddExpenseDescription
            ) ->
        SettingsState(
            appTheme = appTheme,
            monthlyLimit = monthlyLimit,
            loggedInUserEmail = loggedInUserEmail,
            showThemeSelection = showThemeSelection,
            showMonthlyLimitInput = showMonthlyLimitInput,
            showAutoAddExpenseDescription = showAutoAddExpenseDescription
        )
    }.asStateFlow(viewModelScope, SettingsState.INITIAL)

    override fun onThemePreferenceClick() {
        savedStateHandle[SHOW_THEME_SELECTION] = true
    }

    override fun onAppThemeSelectionDismiss() {
        savedStateHandle[SHOW_THEME_SELECTION] = false
    }

    override fun onAppThemeSelectionConfirm(theme: AppTheme) {
        viewModelScope.launch {
            preferencesManager.updateAppTheme(theme)
            savedStateHandle[SHOW_THEME_SELECTION] = false
        }
    }

    override fun onMonthlyLimitPreferenceClick() {
        savedStateHandle[SHOW_MONTHLY_LIMIT_INPUT] = true
    }

    override fun onMonthlyLimitInputDismiss() {
        savedStateHandle[SHOW_MONTHLY_LIMIT_INPUT] = false
    }

    override fun onMonthlyLimitInputConfirm(amount: String) {
        val parsedAmount = amount.toLongOrNull() ?: return
        viewModelScope.launch {
            if (parsedAmount < 0L) {
                eventsChannel.send(
                    SettingsEvent.ShowUiMessage(
                        UiText.StringResource(R.string.error_invalid_amount),
                        true
                    )
                )
                return@launch
            }
            preferencesManager.updateMonthlyLimit(parsedAmount)
            eventsChannel.send(SettingsEvent.ShowUiMessage(UiText.StringResource(R.string.monthly_limit_updated)))
            savedStateHandle[SHOW_MONTHLY_LIMIT_INPUT] = false
        }
    }

    override fun onAutoAddExpenseClick() {
        savedStateHandle[KEY_SHOW_AUTO_ADD_EXPENSE_DESC] = true
    }

    override fun onAutoAddExpenseDismiss() {
        savedStateHandle[KEY_SHOW_AUTO_ADD_EXPENSE_DESC] = false
    }

    override fun onAutoAddExpenseConfirm() {
        viewModelScope.launch {
            savedStateHandle[KEY_SHOW_AUTO_ADD_EXPENSE_DESC] = false
            eventsChannel.send(SettingsEvent.RequestSmsPermission)
        }
    }

    override fun onGoogleAccountSelectionClick() {
        viewModelScope.launch {
            googleAuthClient.signIn()?.let {
                eventsChannel.send(SettingsEvent.LaunchGoogleAccountSelection(it))
            }
        }
    }

    fun onGoogleAccountSelected(result: ActivityResult) {
        val data = result.data ?: return
        viewModelScope.launch {
            val userData = googleAuthClient.signInWithIntent(data) ?: return@launch
            preferencesManager.updateGoogleUserData(userData)
            eventsChannel.send(SettingsEvent.ShowUiMessage(UiText.StringResource(R.string.google_account_added)))
        }
    }

    override fun onPerformBackupClick() {
    }

    override fun onCancelOngoingBackupClick() {
    }

    sealed class SettingsEvent {
        data class ShowUiMessage(val message: UiText, val error: Boolean = false) : SettingsEvent()
        data class LaunchGoogleAccountSelection(val intent: IntentSender) : SettingsEvent()
        object RequestSmsPermission : SettingsEvent()
        object LaunchBackupExportPathSelector : SettingsEvent()
    }
}

private const val SHOW_THEME_SELECTION = "SHOW_THEME_SELECTION"
private const val SHOW_MONTHLY_LIMIT_INPUT = "SHOW_MONTHLY_LIMIT_INPUT"
private const val KEY_SHOW_AUTO_ADD_EXPENSE_DESC = "KEY_SHOW_AUTO_ADD_EXPENSE_DESC"