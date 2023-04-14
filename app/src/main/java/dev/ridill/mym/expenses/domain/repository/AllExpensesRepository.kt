package dev.ridill.mym.expenses.domain.repository

import androidx.compose.ui.graphics.Color
import dev.ridill.mym.expenses.domain.model.Expense
import dev.ridill.mym.expenses.domain.model.TagOverview
import kotlinx.coroutines.flow.Flow

interface AllExpensesRepository {

    fun getYearsList(): Flow<List<String>>

    fun getTotalExpenditure(monthAndYear: String): Flow<Double>

    fun getTagOverviews(totalExpenditure: Double, monthAndYear: String): Flow<List<TagOverview>>

    suspend fun deleteTag(tag: String)

    fun getExpensesByTagForDate(tag: String?, monthAndYear: String): Flow<List<Expense>>

    suspend fun tagExpenses(tag: String?, expenseIds: List<Long>)

    suspend fun createTag(tag: String, color: Color)

    suspend fun deleteExpenses(ids: List<Long>)
}