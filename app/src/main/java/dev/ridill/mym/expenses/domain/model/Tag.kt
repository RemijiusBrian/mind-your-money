package dev.ridill.mym.expenses.domain.model

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.ridill.mym.core.ui.theme.MoneyGreen
import kotlinx.parcelize.Parcelize

data class Tag(
    val name: String,
    val color: Color
) {
    companion object {
        val Untagged = Tag(
            name = UNTAGGED_NAME,
            color = MoneyGreen
        )
    }
}

private const val UNTAGGED_NAME = "Untagged"

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