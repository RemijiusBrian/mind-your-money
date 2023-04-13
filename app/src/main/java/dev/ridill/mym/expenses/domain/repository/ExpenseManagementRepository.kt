package dev.ridill.mym.expenses.domain.repository

import dev.ridill.mym.expenses.domain.model.TagOverview
import kotlinx.coroutines.flow.Flow

interface ExpenseManagementRepository {

    fun getYearsList(): Flow<List<String>>

    fun getTotalExpenditure(monthAndYear: String): Flow<Double>

    fun getTagOverviews(totalExpenditure: Double, monthAndYear: String): Flow<List<TagOverview>>

    suspend fun deleteTag(tag: String)

//    fun getExpenses(tag: String?, monthAndYear: String): Flow<List<Expense>>

//    suspend fun tagExpenses(tag: String?, expenseIds: List<Long>)

//    suspend fun createTag(tag: String, color: Color)

//    suspend fun deleteExpenses(ids: List<Long>)
}