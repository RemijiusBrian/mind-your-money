package dev.ridill.mym.settings.presentation.backup

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
import dev.ridill.mym.core.domain.model.UiText
import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.core.util.asStateFlow
import dev.ridill.mym.core.util.logI
import dev.ridill.mym.settings.domain.back_up.BackupWorkManager
import dev.ridill.mym.settings.domain.back_up.WORK_ERROR_RES_ID
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
class BackupSettingsViewModel @Inject constructor(
    private val backupWorkManager: BackupWorkManager,
    private val authClient: GoogleAuthClient,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), BackupSettingsActions {

    private val signedInAccountEmail = MutableStateFlow<String?>(null)
    private val isGoogleAccountSignedIn = signedInAccountEmail.map { !it.isNullOrEmpty() }
        .distinctUntilChanged()
    private val isBackupInProgress = MutableStateFlow(false)

    private val showRestorationWarning = savedStateHandle
        .getStateFlow(SHOW_RESTORATION_WARNING, false)

    val state = combineTuple(
        signedInAccountEmail,
        isGoogleAccountSignedIn,
        isBackupInProgress,
        showRestorationWarning
    ).map { (
                signedInAccountEmail,
                isGoogleAccountSignedIn,
                isBackupInProgress,
                showRestorationWarning
            ) ->
        BackupSettingsState(
            signedInAccountMail = signedInAccountEmail,
            isGoogleAccountSignedIn = isGoogleAccountSignedIn,
            isBackupInProgress = isBackupInProgress,
            backupInterval = Int.Zero,
            showRestorationWarning = showRestorationWarning
        )
    }.asStateFlow(viewModelScope, BackupSettingsState.INITIAL)

    private val eventsChannel = Channel<BackupSettingsEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    init {
        collectActiveBackupUploadWorkInfo()
//        collectActiveBackupRestorationWorkInfo()
    }

    private fun collectActiveBackupUploadWorkInfo() = viewModelScope.launch {
        backupWorkManager.getActiveUploadWork().asFlow().collectLatest { info ->
            isBackupInProgress.update {
                info?.state == WorkInfo.State.RUNNING
            }

            when (info?.state) {
                WorkInfo.State.SUCCEEDED -> {
                    logI { "Backup Upload Complete" }
                    eventsChannel.send(BackupSettingsEvent.ShowUiMessage(UiText.StringResource(R.string.backup_completed)))
                }

                WorkInfo.State.FAILED -> {
                    info.outputData.getInt(WORK_ERROR_RES_ID, -1).takeIf { it != -1 }?.let {
                        eventsChannel.send(
                            BackupSettingsEvent.ShowUiMessage(
                                UiText.StringResource(it),
                                true
                            )
                        )
                    }
                }

                else -> Unit
            }
        }
    }

    private fun collectActiveBackupRestorationWorkInfo() = viewModelScope.launch {
        backupWorkManager.getActiveRestorationWork().asFlow().collectLatest { info ->
            when (info?.state) {
                WorkInfo.State.SUCCEEDED -> {
                    logI { "Restoration Complete" }
                    eventsChannel.send(BackupSettingsEvent.RestartApplication)
                }

                WorkInfo.State.FAILED -> {
                    info.outputData.getInt(WORK_ERROR_RES_ID, -1).takeIf { it != -1 }?.let {
                        eventsChannel.send(
                            BackupSettingsEvent.ShowUiMessage(
                                UiText.StringResource(it),
                                true
                            )
                        )
                    }
                }

                else -> Unit
            }
        }
    }

    override fun getSignedInAccountDetails() {
        signedInAccountEmail.update {
            authClient.getSignedInAccount()?.email
        }
    }

    override fun onGoogleAccountClick() {
        viewModelScope.launch {
            val intent = authClient.getSignInIntent()
            eventsChannel.send(BackupSettingsEvent.StartGoogleSignInIntent(intent))
        }
    }

    fun onGoogleAccountSelected(result: ActivityResult) {
        viewModelScope.launch {
            val userData = authClient.signInWithResult(result)
            if (userData == null) {
                signedInAccountEmail.update {
                    authClient.getSignedInAccount()?.email
                }
                eventsChannel.send(
                    BackupSettingsEvent.ShowUiMessage(
                        UiText.StringResource(R.string.error_google_account_linking_failed),
                        true
                    )
                )
            }
            signedInAccountEmail.update { userData?.email }
        }
    }

    override fun onBackupNowClick() {
        backupWorkManager.startOneTimeBackupWork()
    }

    override fun onRestoreBackupClick() {
        savedStateHandle[SHOW_RESTORATION_WARNING] = true
    }

    override fun onRestorationWarningDismiss() {
        savedStateHandle[SHOW_RESTORATION_WARNING] = false
    }

    override fun onRestorationWarningConfirm() {
        viewModelScope.launch {
            savedStateHandle[SHOW_RESTORATION_WARNING] = false
            backupWorkManager.startBackupRestorationWork().asFlow().collectLatest { info ->
                when (info?.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        logI { "Restoration Complete" }
                        eventsChannel.send(BackupSettingsEvent.RestartApplication)
                    }

                    WorkInfo.State.FAILED -> {
                        info.outputData.getInt(WORK_ERROR_RES_ID, -1).takeIf { it != -1 }?.let {
                            eventsChannel.send(
                                BackupSettingsEvent.ShowUiMessage(
                                    UiText.StringResource(it),
                                    true
                                )
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    sealed class BackupSettingsEvent {
        data class StartGoogleSignInIntent(val intent: Intent) : BackupSettingsEvent()
        data class ShowUiMessage(val message: UiText, val isError: Boolean = false) :
            BackupSettingsEvent()

        object RestartApplication : BackupSettingsEvent()
    }
}

private const val SHOW_RESTORATION_WARNING = "SHOW_RESTORATION_WARNING"