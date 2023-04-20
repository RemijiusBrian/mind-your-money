package dev.ridill.mym.settings.data.remote

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GDriveApi {

    @POST("upload/drive/v3/files")
    suspend fun uploadBackupFileToDrive(
        @Header("Authorization") userToken: String,
        @Body backupFile: RequestBody,
        @Query("uploadType") uploadType: String = UPLOAD_TYPE_MULTIPART
    )
}

private const val UPLOAD_TYPE_MULTIPART = "multipart"