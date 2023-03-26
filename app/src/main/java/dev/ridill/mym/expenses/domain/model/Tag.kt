package dev.ridill.mym.expenses.domain.model

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.parcelize.Parcelize

data class Tag(
    val name: String,
    val color: Color
)

@Parcelize
data class TagInput(
    val name: String,
    val colorCode: Int
) : Parcelable {
    companion object {
        val INITIAL = TagInput(
            name = "",
            colorCode = TagColors.first().toArgb()
        )
    }
}