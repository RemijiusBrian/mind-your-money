package dev.ridill.mym.settings.domain.back_up

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.time.Duration

class BackupWorkManager(
    context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun startOneTimeBackupWork() {
        val workRequest = OneTimeWorkRequestBuilder<BackupUploadWorker>()
            .setConstraints(buildBackupConstraints())
            .build()

        workManager.beginUniqueWork(
            BACKUP_UPLOAD_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
            .enqueue()
    }

    fun schedulePeriodicBackupWork(intervalInDays: Long) {
        val workRequest =
            PeriodicWorkRequestBuilder<BackupUploadWorker>(Duration.ofDays(intervalInDays))
                .setConstraints(buildBackupConstraints())
                .build()

        workManager.enqueueUniquePeriodicWork(
            BACKUP_UPLOAD_WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
    }

    fun getActiveUploadWork(): LiveData<WorkInfo?> = workManager
        .getWorkInfosForUniqueWorkLiveData(BACKUP_UPLOAD_WORK_NAME)
        .map { it.firstOrNull() }

    fun startBackupRestorationWork(): LiveData<WorkInfo?> {
        val workRequest = OneTimeWorkRequestBuilder<BackupRestoreWorker>()
            .setConstraints(buildBackupConstraints())
            .build()

        workManager.enqueueUniqueWork(
            BACKUP_RESTORATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        return workManager.getWorkInfosForUniqueWorkLiveData(BACKUP_RESTORATION_WORK_NAME)
            .map { infoList ->
                infoList.find { it.id == workRequest.id }
            }
    }

    fun getActiveRestorationWork(): LiveData<WorkInfo?> = workManager
        .getWorkInfosForUniqueWorkLiveData(BACKUP_RESTORATION_WORK_NAME)
        .map { it.firstOrNull() }

    private fun buildBackupConstraints(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
}

private const val BACKUP_UPLOAD_WORK_NAME = "BACKUP_UPLOAD_WORK_NAME"
private const val BACKUP_RESTORATION_WORK_NAME = "BACKUP_RESTORATION_WORK_NAME"