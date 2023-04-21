package dev.ridill.mym.settings.domain.back_up

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.await
import java.util.UUID

class BackupWorkManager(
    context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    companion object {
        const val BACKUP_WORK_NAME = "BACKUP_WORK"
    }

    fun performRemoteBackup(): LiveData<WorkInfo?> {
        val workRequest = OneTimeWorkRequestBuilder<GDriveBackupWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.beginUniqueWork(BACKUP_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
            .enqueue()

        return workManager.getWorkInfoByIdLiveData(workRequest.id)
    }

    fun getWorkInfoById(id: UUID): LiveData<WorkInfo> =
        workManager.getWorkInfoByIdLiveData(id)

    suspend fun getActiveWorks(): List<WorkInfo> {
        val workQuery = WorkQuery.Builder
            .fromStates(listOf(WorkInfo.State.RUNNING))
            .addUniqueWorkNames(listOf(BACKUP_WORK_NAME))
            .build()
        return workManager.getWorkInfos(workQuery).await()
    }
}