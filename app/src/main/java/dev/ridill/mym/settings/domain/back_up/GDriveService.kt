package dev.ridill.mym.settings.domain.back_up

import android.accounts.Account
import android.content.Context
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.core.util.tryOrNull
import dev.ridill.mym.settings.data.remote.GDriveApi
import dev.ridill.mym.settings.data.remote.dto.BackupFileMetadataDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.time.ZoneId

class GDriveService(
    private val context: Context,
    private val api: GDriveApi
) {
    companion object {
        const val SCOPE_STRING = Scopes.DRIVE_APPFOLDER
        val Scope: Scope get() = Scope(SCOPE_STRING)
    }

    suspend fun getAuthToken(account: Account): String? = withContext(Dispatchers.IO) {
        tryOrNull {
            GoogleAuthUtil.getToken(context, account, "oauth2:$SCOPE_STRING")
        }
    }

    suspend fun uploadFile(
        file: File,
        userTokenKey: String
    ) = withContext(Dispatchers.IO) {
        val metadata = BackupFileMetadataDto(
            name = DateUtil.currentDateTime().atZone(ZoneId.systemDefault()).toEpochSecond()
                .toString(),
            mimeType = BackupService.MIME_TYPE_OCTET_STREAM,
            parents = emptyList()
        )
        val requestBody = RequestBody.create(
            MediaType.parse(BackupService.MIME_TYPE_OCTET_STREAM),
            file
        )
        api.uploadBackupFileToDrive(
            userToken = "Bearer $userTokenKey",
            backupFile = requestBody,
        )
    }
}