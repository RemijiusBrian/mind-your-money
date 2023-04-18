package dev.ridill.mym.settings.data.remote

import dev.ridill.mym.settings.data.remote.dto.BackupFileMetadataDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface GDriveApi {

    @GET("auth/drive.appdata")
    suspend fun requestAccess()

    @Multipart
    @POST("upload/drive/v3/files")
    suspend fun uploadBackupFileToDrive(
        @Body metadata: BackupFileMetadataDto,
        @Part backupFile: MultipartBody.Part,
        @Query("uploadType") uploadType: String = UPLOAD_TYPE_MULTIPART
    )
}

private const val UPLOAD_TYPE_MULTIPART = "multipart"