package dev.ridill.mym.settings.domain.back_up

import android.content.Context
import dev.ridill.mym.core.data.db.MYMDatabase
import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.core.util.tryOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZoneId

class BackupService(
    private val context: Context
) {
    companion object {
        const val MIME_TYPE_OCTET_STREAM = "application/octet-stream"
    }

    suspend fun getBackupFile(): File? = withContext(Dispatchers.IO) {
        tryOrNull {
            val backupFile = File(
                context.cacheDir,
                "${
                    DateUtil.currentDateTime()
                        .atZone(ZoneId.systemDefault())
                        .toEpochSecond()
                }.backup"
            )
            val dbFile = context.getDatabasePath(MYMDatabase.NAME)
            dbFile.inputStream()
                .copyTo(backupFile.outputStream())

            backupFile
        }
    }
}

