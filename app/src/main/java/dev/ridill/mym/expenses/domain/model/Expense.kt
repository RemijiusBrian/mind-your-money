package dev.ridill.mym.expenses.domain.model

import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.core.util.Formatter
import java.time.LocalDateTime

data class Expense(
    val id: Long = 0L,
    val note: String,
    val amount: Double,
    val date: LocalDateTime,
    val tag: Tag? = null
) {
    val dateFormatted: String
        get() = date.format(DateUtil.Formatters.dayShortNameAndDayNumber)

    val amountFormatted: String
        get() = Formatter.currency(amount)
}