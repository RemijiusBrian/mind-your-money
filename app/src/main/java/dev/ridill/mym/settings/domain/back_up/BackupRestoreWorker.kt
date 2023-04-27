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
import dev.ridill.mym.core.util.logE
import dev.ridill.mym.core.util.logI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

@HiltWorker
class BackupRestoreWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val backupService: BackupService,
    private val notificationManager: BackupNotificationManager
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        startForegroundService()
        try {
            logI { "Starting Restore Job" }
            backupService.restoreLatestBackup()
            Result.success()
        } catch (t: AccountAccessThrowable) {
            logE(t) { "Backup Error" }
            Result.failure(
                workDataOf(
                    WORK_ERROR_RES_ID to R.string.error_google_account_access_failed
                )
            )
        } catch (t: BackupThrowable) {
            logE(t) { "Backup Error" }
            Result.failure(
                workDataOf(
                    WORK_ERROR_RES_ID to R.string.error_backup_failed
                )
            )
        } catch (t: Throwable) {
            logE(t) { "Backup Error" }
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
                notificationManager.buildForegroundNotification(R.string.notif_backup_restoration_in_progress)
            )
        )
    }
}