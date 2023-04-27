package dev.ridill.mym.settings.data.remote

import dev.ridill.mym.settings.data.remote.dto.GDriveFile
import dev.ridill.mym.settings.data.remote.dto.GDriveFilesListResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface GDriveApi {

    @Multipart
    @POST("upload/drive/v3/files")
    suspend fun uploadBackupFileToDrive(
        @Header("Authorization") authToken: String,
        @Query("uploadType") uploadType: String,
        @Part("Metadata") metadataDto: RequestBody,
        @Part("Media") backupFile: RequestBody
    ): GDriveFile

    @GET("drive/v3/files")
    suspend fun getFilesList(
        @Header("Authorization") userToken: String,
        @Query("orderBy") orderBy: String,
        @Query("spaces") spaces: String,
        @Query("q") query: String
    ): GDriveFilesListResponse

    @GET("drive/v3/files/{fileId}?alt=media")
    suspend fun downloadFile(
        @Header("Authorization") userToken: String,
        @Path("fileId") fileId: String
    ): ResponseBody
}