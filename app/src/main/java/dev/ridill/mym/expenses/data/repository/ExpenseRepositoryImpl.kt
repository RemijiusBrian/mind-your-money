package dev.ridill.mym.expenses.data.repository

import dev.ridill.mym.core.util.DispatcherProvider
import dev.ridill.mym.expenses.data.local.ExpenseDao
import dev.ridill.mym.expenses.data.local.entity.ExpenseEntity
import dev.ridill.mym.expenses.data.local.relation.ExpenseAndTagRelation
import dev.ridill.mym.expenses.domain.model.Expense
import dev.ridill.mym.expenses.domain.model.ExpenseListItem
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExpenseRepositoryImpl(
    private val dao: ExpenseDao,
    private val dispatcher: DispatcherProvider
) : ExpenseRepository {

    override fun getExpenseForDate(monthHyphenYearString: String): Flow<List<ExpenseListItem>> =
        dao.getExpensesForDate(monthHyphenYearString).map { entities ->
            entities.map(ExpenseAndTagRelation::toExpenseListItem)
        }

    override fun getExpenditureForDate(monthHyphenYearString: String): Flow<Double> =
        dao.getExpenditureForDate(monthHyphenYearString)

    override suspend fun insert(expense: Expense): Long = withContext(dispatcher.io) {
        dao.insert(expense.toEntity()).first()
    }

    override suspend fun getExpenseById(id: Long): Expense? = withContext(dispatcher.io) {
        dao.getExpenseById(id)?.toExpense()
    }

    override suspend fun delete(expense: Expense) = withContext(dispatcher.io) {
        dao.delete(expense.toEntity())
    }

    override fun getDistinctYearsList(): Flow<List<Int>> = dao.getDistinctYears()

    override fun getExpenseByTagForDate(
        tag: String?,
        monthHyphenYearString: String
    ): Flow<List<Expense>> = dao.getExpensesByTagForDate(tag, monthHyphenYearString)
        .map { entities ->
            entities.map(ExpenseEntity::toExpense)
        }

    override suspend fun deleteMultipleExpenses(ids: List<Long>) = withContext(dispatcher.io) {
        dao.deleteMultipleExpenses(ids)
    }

    override suspend fun setTagToExpenses(
        tag: String?,
        expenseIds: List<Long>
    ) = withContext(dispatcher.io) {
        dao.setTagToExpenses(tag, expenseIds)
    }
}