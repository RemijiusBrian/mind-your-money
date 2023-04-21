package dev.ridill.mym.settings.domain.back_up

import android.content.Context
import dev.ridill.mym.core.data.db.MYMDatabase
import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.core.util.logD
import dev.ridill.mym.core.util.logI
import dev.ridill.mym.core.util.tryOrNull
import dev.ridill.mym.settings.data.remote.dto.BackupFileMetadataDto
import dev.ridill.mym.settings.presentation.sign_in.GoogleAuthClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZoneId

class BackupService(
    private val context: Context,
    private val gDriveService: GDriveService,
    private val authClient: GoogleAuthClient
) {
    private suspend fun getBackupFile(): File? = withContext(Dispatchers.IO) {
        tryOrNull {
            val backupFile = File(
                context.cacheDir,
                "MYM-backup-temp.backup"
            )
            val dbFile = context.getDatabasePath(MYMDatabase.NAME)
            dbFile.inputStream()
                .copyTo(backupFile.outputStream())

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
    suspend fun uploadBackup() = withContext(Dispatchers.IO) {
        val backupFile = getBackupFile() ?: throw BackupCreationThrowable()
        val authToken = getAuthToken()

        val epochSecond = DateUtil.currentDateTime().atZone(ZoneId.systemDefault()).toEpochSecond()
        val fileName = "MYM-${epochSecond}.backup"
        val metadata = BackupFileMetadataDto(
            name = fileName,
            mimeType = MimeType.OCTET_STREAM,
            parents = listOf(GDriveService.APP_DATA_FOLDER)
        )
        logD { "Auth Token - $authToken" }
        val result = gDriveService.uploadFile(
            authToken = authToken,
            metadataDto = metadata,
            file = backupFile
        )
        logI { "Uploaded - ${result.name}" }
    }

    @Throws(AccountAccessThrowable::class, NoBackupFoundThrowable::class)
    suspend fun downloadLatestBackupFile() {
        val token = getAuthToken()
        val filesList = gDriveService.getFilesList(
            authToken = token
        )
        logD { "Files List - $filesList" }
        val latestBackup = filesList.firstOrNull() ?: throw NoBackupFoundThrowable()
        logD { "Latest Backup - ${latestBackup.name}" }
        val backupFile = gDriveService.downloadFile(
            authToken = token,
            id = latestBackup.id
        )
        val bytes = backupFile.bytes()
        logD { "Bytes - ${bytes.decodeToString()}" }
    }

    object MimeType {
        const val OCTET_STREAM = "application/octet-stream"
        const val JSON = "application/json"
//        const val TEXT = "text/plain"
    }
}

class BackupCreationThrowable : Throwable("Failed to create backup")
class AccountAccessThrowable : Throwable("Could not access your google Account")
class BackupThrowable : Throwable("Backup Failed")
class NoBackupFoundThrowable : Throwable("No Backup File Found")