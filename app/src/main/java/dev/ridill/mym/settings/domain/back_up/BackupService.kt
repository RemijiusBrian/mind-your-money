package dev.ridill.mym.settings.domain.back_up

import android.content.Context
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import dev.ridill.mym.core.data.db.MYMDatabase
import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.core.util.logD
import dev.ridill.mym.core.util.tryOrNull
import java.io.File
import java.time.ZoneId

class BackupService(
    private val context: Context
) {
    companion object {
        const val MIME_TYPE_OCTET_STREAM = "application/octet-stream"
    }

    fun getBackupFile(): File? = tryOrNull {
        val backupFile = File(
            context.cacheDir,
            "${DateUtil.currentDateTime().atZone(ZoneId.systemDefault()).toEpochSecond()}.backup"
        )
        val dbFile = context.getDatabasePath(MYMDatabase.NAME)
        logD { "Db File - $dbFile" }
        dbFile.inputStream()
            .copyTo(backupFile.outputStream())
        backupFile
    }
}