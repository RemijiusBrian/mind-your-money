package dev.ridill.mym.settings.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class BackupFileMetadataDto(
    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("mimeType")
    val mimeType: String,

    @Expose
    @SerializedName("parents")
    val parents: List<String>
)