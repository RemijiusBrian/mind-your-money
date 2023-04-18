package dev.ridill.mym.settings.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dev.ridill.mym.settings.domain.back_up.BackupService

@Keep
data class BackupFileMetadataDto(
    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("mimeType")
    val mimeType: String = BackupService.MIME_TYPE_OCTET_STREAM,

    @Expose
    @SerializedName("parents")
    val parents: List<String> = listOf(APP_DATA_FOLDER)
)

private const val APP_DATA_FOLDER = "appDataFolder"