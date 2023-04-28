package dev.ridill.mym.expenses.domain.repository

import dev.ridill.mym.expenses.domain.model.Expense
import dev.ridill.mym.expenses.domain.model.ExpenseListItem
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    fun getExpenseForDate(
        monthHyphenYearString: String
    ): Flow<List<ExpenseListItem>>

    fun getExpenditureForDate(
        monthHyphenYearString: String
    ): Flow<Double>

    suspend fun insert(expense: Expense): Long

    suspend fun getExpenseById(id: Long): Expense?

    suspend fun delete(expense: Expense)

    suspend fun deleteMultipleExpenses(ids: List<Long>)

    fun getDistinctYearsList(): Flow<List<Int>>

    fun getExpenseByTagForDate(tag: String?, monthHyphenYearString: String): Flow<List<Expense>>

    suspend fun setTagToExpenses(tag: String?, expenseIds: List<Long>)
}