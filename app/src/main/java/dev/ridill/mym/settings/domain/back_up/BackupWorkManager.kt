package dev.ridill.mym.settings.domain.back_up

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager

class BackupWorkManager(
    context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    companion object {
        const val BACKUP_WORK_NAME = "BACKUP_WORK"
    }

    fun startBackupWork() {
        val workRequest = OneTimeWorkRequestBuilder<GDriveBackupWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.beginUniqueWork(BACKUP_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
            .enqueue()
    }

    fun getActiveWorks(): LiveData<WorkInfo?> =
        workManager.getWorkInfosForUniqueWorkLiveData(BACKUP_WORK_NAME)
            .map { infos ->
                infos.find { it.state == WorkInfo.State.RUNNING }
            }
}