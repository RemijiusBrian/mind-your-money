package dev.ridill.mym.settings.domain.back_up

import android.accounts.Account
import android.content.Context
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import dev.ridill.mym.core.util.toJson
import dev.ridill.mym.core.util.tryOrNull
import dev.ridill.mym.settings.data.remote.GDriveApi
import dev.ridill.mym.settings.data.remote.dto.BackupFileMetadataDto
import dev.ridill.mym.settings.data.remote.dto.GDriveFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File

class GDriveService(
    private val context: Context,
    private val api: GDriveApi
) {
    companion object {
        const val SCOPE_STRING = Scopes.DRIVE_APPFOLDER
        val Scope: Scope get() = Scope(SCOPE_STRING)

        const val APP_DATA_FOLDER = "appDataFolder"
    }

    suspend fun getAuthToken(account: Account): String? = withContext(Dispatchers.IO) {
        tryOrNull {
            GoogleAuthUtil.getToken(context, account, "oauth2:$SCOPE_STRING")
        }
    }

    @Throws(BackupThrowable::class, Throwable::class)
    suspend fun uploadFile(
        metadataDto: BackupFileMetadataDto,
        file: File,
        authToken: String,
        uploadType: String,
    ): GDriveFile = withContext(Dispatchers.IO) {
        val metadataBody = metadataDto.toJson()?.let { json ->
            RequestBody.create(
                MediaType.parse(BackupService.MimeType.JSON),
                json
            )
        } ?: throw BackupThrowable()
        val requestBody = RequestBody.create(
            MediaType.parse(BackupService.MimeType.OCTET_STREAM),
            file
        )

        api.uploadBackupFileToDrive(
            authToken = authToken,
            uploadType = uploadType,
            backupFile = requestBody,
            metadataDto = metadataBody
        )
    }

    suspend fun getFilesList(
        authToken: String,
        orderBy: String,
        spaces: String,
        query: String
    ): List<GDriveFile> = api.getFilesList(
        userToken = authToken,
        orderBy = orderBy,
        spaces = spaces,
        query = query
    ).gDriveFiles

    suspend fun downloadFile(
        authToken: String,
        id: String
    ): ResponseBody = api.downloadFile(
        userToken = authToken,
        fileId = id
    )
}