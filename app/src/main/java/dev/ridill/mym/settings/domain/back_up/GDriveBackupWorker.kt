package dev.ridill.mym.settings.domain.back_up

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ridill.mym.core.util.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@HiltWorker
class GDriveBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val backupService: BackupService,
    private val notificationManager: BackupNotificationManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        startForegroundService()
        val backupFile = backupService.getBackupFile()
        logD { "Backup File - $backupFile" }

        delay(10.seconds)

        backupFile?.inputStream()?.use {
            logD { "Content - ${it.readBytes().decodeToString()}" }
        }
        Result.success()
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