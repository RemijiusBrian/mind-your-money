package dev.ridill.mym.expenses.domain.model

import android.os.Parcelable
import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.core.util.Empty
import dev.ridill.mym.core.util.Formatter
import dev.ridill.mym.core.util.Zero
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Expense(
    val id: Long,
    val note: String,
    val amount: String,
    val dateTime: LocalDateTime,
    val tagName: String?
) : Parcelable {

    val amountFormatted: String
        get() = Formatter.compactCurrency(amount.toDoubleOrNull() ?: Double.Zero)

    companion object {
        val DEFAULT = Expense(
            id = Long.Zero,
            note = String.Empty,
            amount = String.Empty,
            dateTime = DateUtil.currentDateTime(),
            tagName = null
        )
    }
}