package dev.ridill.mym.expenses.domain.repository

import dev.ridill.mym.expenses.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    fun getExpenseForDate(
        monthHyphenYearString: String
    ): Flow<List<Expense>>

    fun getExpenditureForDate(
        monthHyphenYearString: String
    ): Flow<Double>

    suspend fun insert(expense: Expense)
}