package dev.ridill.mym.settings.presentation.settings

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.mym.R
import dev.ridill.mym.core.data.preferences.PreferencesManager
import dev.ridill.mym.core.domain.model.AppTheme
import dev.ridill.mym.core.domain.model.UiText
import dev.ridill.mym.core.util.asStateFlow
import dev.ridill.mym.settings.domain.back_up.BackupManager
import dev.ridill.mym.settings.domain.back_up.WORK_ERROR_RES_ID
import dev.ridill.mym.settings.presentation.sign_in.GoogleAuthClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferencesManager: PreferencesManager,
    private val googleAuthClient: GoogleAuthClient,
    private val backupManager: BackupManager
) : ViewModel(), SettingsActions {

    private val preferences = preferencesManager.preferences
    private val appTheme = preferences.map { it.theme }.distinctUntilChanged()
    private val monthlyLimit = preferences.map { it.monthlyLimit }.distinctUntilChanged()
    private val loggedInUserEmail = savedStateHandle.getStateFlow<String?>(SIGNED_IN_USER, null)

    private val showThemeSelection = savedStateHandle
        .getStateFlow(SHOW_THEME_SELECTION, false)
    private val showMonthlyLimitInput = savedStateHandle
        .getStateFlow(SHOW_MONTHLY_LIMIT_INPUT, false)

    private val showAutoAddExpenseDescription =
        savedStateHandle.getStateFlow(KEY_SHOW_AUTO_ADD_EXPENSE_DESC, false)

    private val isBackupInProgress = MutableStateFlow(false)

    private val eventsChannel = Channel<SettingsEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    val state = combineTuple(
        appTheme,
        monthlyLimit,
        loggedInUserEmail,
        showThemeSelection,
        showMonthlyLimitInput,
        showAutoAddExpenseDescription,
        isBackupInProgress
    ).map { (
                appTheme,
                monthlyLimit,
                loggedInUserEmail,
                showThemeSelection,
                showMonthlyLimitInput,
                showAutoAddExpenseDescription,
                isBackupInProgress
            ) ->
        SettingsState(
            appTheme = appTheme,
            monthlyLimit = monthlyLimit,
            loggedInUserEmail = loggedInUserEmail,
            showThemeSelection = showThemeSelection,
            showMonthlyLimitInput = showMonthlyLimitInput,
            showAutoAddExpenseDescription = showAutoAddExpenseDescription,
            isBackupInProgress = isBackupInProgress
        )
    }.asStateFlow(viewModelScope, SettingsState.INITIAL)

    init {
        updateSignedInUser()
        checkActiveBackupJob()
    }

    private fun updateSignedInUser() = viewModelScope.launch {
        googleAuthClient.getSignedInUser()?.let {
            savedStateHandle[SIGNED_IN_USER] = it.email
        }
    }

    private fun checkActiveBackupJob() = viewModelScope.launch {
        backupManager.getActiveWorks().firstOrNull()?.let {
            backupManager.getWorkInfoById(it.id).asFlow().collectLatest { info ->
                isBackupInProgress.update { info.state == WorkInfo.State.RUNNING }
            }
        }
    }

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
            eventsChannel.send(
                SettingsEvent.LaunchGoogleAccountSelection(
                    googleAuthClient.getSignInIntent()
                )
            )
        }
    }

    fun onGoogleAccountSelected(result: ActivityResult) {
        val data = result.data ?: return
        viewModelScope.launch {
            val userData = googleAuthClient.signInWithIntent(data)
            if (userData == null) {
                eventsChannel.send(SettingsEvent.ShowUiMessage(UiText.StringResource(R.string.error_google_account_linking_failed)))
                return@launch
            }
            updateSignedInUser()
            eventsChannel.send(SettingsEvent.ShowUiMessage(UiText.StringResource(R.string.google_account_added)))
        }
    }

    private var backupJob: Job? = null
    override fun onPerformBackupClick() {
        backupJob?.cancel()
        if (backupJob?.isActive == true) return
        backupJob = viewModelScope.launch {
            backupManager.performRemoteBackup().asFlow().collectLatest { info ->
                isBackupInProgress.update {
                    info?.state == WorkInfo.State.RUNNING
                }
                when (info?.state) {
//                    WorkInfo.State.ENQUEUED -> {}
//                    WorkInfo.State.RUNNING -> {}
                    WorkInfo.State.SUCCEEDED -> {
                        eventsChannel.send(SettingsEvent.ShowUiMessage(UiText.StringResource(R.string.backup_completed)))
                    }

                    WorkInfo.State.FAILED -> {
                        val errorRes = info.outputData.getInt(WORK_ERROR_RES_ID, -1)
                        if (errorRes != -1) {
                            eventsChannel.send(
                                SettingsEvent.ShowUiMessage(
                                    UiText.StringResource(errorRes),
                                    true
                                )
                            )
                        }
                    }
//                    WorkInfo.State.BLOCKED -> {}
//                    WorkInfo.State.CANCELLED -> {}
//                    null -> {}
                    else -> {}
                }

                if (info?.state == WorkInfo.State.SUCCEEDED
                    || info?.state == WorkInfo.State.CANCELLED
                    || info?.state == WorkInfo.State.FAILED
                ) this.cancel()
            }
        }
    }

    sealed class SettingsEvent {
        data class ShowUiMessage(val message: UiText, val error: Boolean = false) : SettingsEvent()
        data class LaunchGoogleAccountSelection(val intent: Intent) : SettingsEvent()
        object RequestSmsPermission : SettingsEvent()
        object LaunchBackupExportPathSelector : SettingsEvent()
    }
}

private const val SHOW_THEME_SELECTION = "SHOW_THEME_SELECTION"
private const val SHOW_MONTHLY_LIMIT_INPUT = "SHOW_MONTHLY_LIMIT_INPUT"
private const val KEY_SHOW_AUTO_ADD_EXPENSE_DESC = "KEY_SHOW_AUTO_ADD_EXPENSE_DESC"
private const val SIGNED_IN_USER = "SIGNED_IN_USER"