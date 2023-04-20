package dev.ridill.mym.settings.domain.back_up

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ridill.mym.R
import dev.ridill.mym.core.util.logD
import dev.ridill.mym.core.util.logI
import dev.ridill.mym.settings.presentation.sign_in.GoogleAuthClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

@HiltWorker
class GDriveBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val backupService: BackupService,
    private val notificationManager: BackupNotificationManager,
    private val gDriveService: GDriveService,
    private val googleAuthClient: GoogleAuthClient
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            startForegroundService()
            logI { "Starting Backup Work" }
            val backupFile = backupService.getBackupFile() ?: throw BackupCreationThrowable()
            val account = googleAuthClient.getSignedInAccount() ?: throw AccountAccessThrowable()
            val token = gDriveService.getAuthToken(account.account!!)
                ?: throw AccountAccessThrowable()
            logD { "User Token - $token" }
            gDriveService.uploadFile(
                file = backupFile,
                userTokenKey = token
            )

            Result.success()
        } catch (t: BackupCreationThrowable) {
            Result.failure(
                workDataOf(
                    WORK_ERROR_RES_ID to R.string.error_backup_creation_failed
                )
            )
        } catch (t: AccountAccessThrowable) {
            Result.failure(
                workDataOf(
                    WORK_ERROR_RES_ID to R.string.error_google_account_access_failed
                )
            )
        } catch (t: Throwable) {
            Result.failure(
                workDataOf(
                    WORK_ERROR_RES_ID to R.string.error_backup_failed
                )
            )
        }
    }

    private suspend fun startForegroundService() {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                notificationManager.buildForegroundNotification()
            )
        )
    }
}

const val WORK_ERROR_RES_ID = "WORK_ERROR_RES_ID"

class BackupCreationThrowable : Throwable("Failed to create backup")
class AccountAccessThrowable : Throwable("Could not access your google Account")