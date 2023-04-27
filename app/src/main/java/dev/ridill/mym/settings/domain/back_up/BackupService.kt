package dev.ridill.mym.settings.domain.back_up

import android.content.Context
import dev.ridill.mym.core.data.db.MYMDatabase
import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.core.util.logD
import dev.ridill.mym.core.util.logI
import dev.ridill.mym.core.util.tryOrNull
import dev.ridill.mym.settings.data.remote.dto.BackupFileMetadataDto
import dev.ridill.mym.settings.data.remote.dto.GDriveFile
import dev.ridill.mym.settings.presentation.backup.GoogleAuthClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZoneId

class BackupService(
    private val context: Context,
    private val gDriveService: GDriveService,
    private val authClient: GoogleAuthClient,
    private val db: MYMDatabase
) {
    @Throws(BackupCreationThrowable::class)
    private suspend fun getBackupFile(): File? = withContext(Dispatchers.IO) {
        tryOrNull {
            val backupFile = File(
                context.cacheDir,
                "MYM-backup-temp.backup"
            )
            val dbFilePath = db.openHelper.readableDatabase.path ?: throw BackupCreationThrowable()
//                context.getDatabasePath(MYMDatabase.NAME)
            logD { "DB File Path - $dbFilePath" }

            if (backupFile.exists()) {
                backupFile.delete()
                backupFile.createNewFile()
            }

            File(dbFilePath).inputStream()
                .copyTo(backupFile.outputStream())

            backupFile.inputStream().use {
                val data = it.readBytes()
                logD { "DB Content - ${data.contentToString()}" }
            }
            backupFile
        }
    }

    @Throws(AccountAccessThrowable::class)
    private suspend fun getAuthToken(): String {
        val signedInAccount = authClient.getSignedInAccount() ?: throw AccountAccessThrowable()
        val token = gDriveService.getAuthToken(signedInAccount.account!!)
            ?: throw AccountAccessThrowable()

        return "Bearer $token"
    }

    @Throws(
        BackupCreationThrowable::class,
        AccountAccessThrowable::class,
        BackupThrowable::class,
        Throwable::class
    )
    suspend fun uploadBackup(): GDriveFile = withContext(Dispatchers.IO) {
        val backupFile = getBackupFile() ?: throw BackupCreationThrowable()
        val authToken = getAuthToken()

        val epochSecond = DateUtil.currentDateTime().atZone(ZoneId.systemDefault()).toEpochSecond()
        val fileName = "$BACKUP_FILE_PREFIX${epochSecond}.backup"
        val metadata = BackupFileMetadataDto(
            name = fileName,
            mimeType = MimeType.OCTET_STREAM,
            parents = listOf(GDriveService.APP_DATA_FOLDER)
        )
        logD { "Auth Token - $authToken" }
        val result = gDriveService.uploadFile(
            authToken = authToken,
            metadataDto = metadata,
            file = backupFile,
            uploadType = UPLOAD_TYPE_MULTIPART
        )
        logI { "Uploaded - ${result.name}" }
        result
    }

    @Throws(AccountAccessThrowable::class, NoBackupFoundThrowable::class)
    suspend fun restoreLatestBackup() = withContext(Dispatchers.IO) {
        val token = getAuthToken()
        val latestBackup = getLatestBackup(token)
        logD { "Latest Backup - ${latestBackup.name}" }
        val response = gDriveService.downloadFile(
            authToken = token,
            id = latestBackup.id
        )
        db.close()
        val dbPath = db.openHelper.writableDatabase.path ?: throw RestoreFailureThrowable()
//        db.openHelper.setWriteAheadLoggingEnabled(false)
        response.byteStream().use { inputStream ->
            logD { "Downloaded Content - ${inputStream.readBytes().contentToString()}" }
            File(dbPath).outputStream().use { outputStream ->
                outputStream.channel.truncate(0L)
                inputStream.copyTo(outputStream)
            }
        }
        logI { "Data Copied" }
    }

    @kotlin.jvm.Throws(NoBackupFoundThrowable::class)
    private suspend fun getLatestBackup(authToken: String): GDriveFile {
        val filesList = gDriveService.getFilesList(
            authToken = authToken,
            orderBy = ORDER_BY_RECENCY_DESC,
            spaces = GDriveService.APP_DATA_FOLDER,
            query = "name contains \'${BACKUP_FILE_PREFIX}\'"
        )
        logD { "Files List - $filesList" }
        return filesList.firstOrNull() ?: throw NoBackupFoundThrowable()
    }

    object MimeType {
        const val OCTET_STREAM = "application/octet-stream"
        const val JSON = "application/json"
//        const val TEXT = "text/plain"
    }
}

private const val UPLOAD_TYPE_MULTIPART = "multipart"
private const val ORDER_BY_RECENCY_DESC = "recency desc"
private const val BACKUP_FILE_PREFIX = "MYM-"

class BackupCreationThrowable : Throwable("Failed to create backup")
class AccountAccessThrowable : Throwable("Could not access your google Account")
class BackupThrowable : Throwable("Backup Failed")
class NoBackupFoundThrowable : Throwable("No Backup File Found")
class RestoreFailureThrowable : Throwable("Restoration Failed")