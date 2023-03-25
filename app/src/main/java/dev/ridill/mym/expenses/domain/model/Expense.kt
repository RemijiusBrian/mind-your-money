package dev.ridill.mym.expenses.domain.model

import java.time.LocalDateTime

data class Expense(
    val id: Long = 0L,
    val note: String,
    val amount: Double,
    val date: LocalDateTime,
    val tag: Tag? = null
)