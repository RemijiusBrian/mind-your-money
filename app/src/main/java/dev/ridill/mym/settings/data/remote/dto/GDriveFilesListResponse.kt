package dev.ridill.mym.settings.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class GDriveFilesListResponse(
    @Expose
    @SerializedName("files")
    val gDriveFiles: List<GDriveFile>,

    @Expose
    @SerializedName("incompleteSearch")
    val incompleteSearch: Boolean,

    @Expose
    @SerializedName("kind")
    val kind: String
)

@Keep
data class GDriveFile(
    @Expose
    @SerializedName("id")
    val id: String,

    @Expose
    @SerializedName("kind")
    val kind: String,

    @Expose
    @SerializedName("mimeType")
    val mimeType: String,

    @Expose
    @SerializedName("name")
    val name: String
)