package dev.ridill.mym.expenses.data.repository

import dev.ridill.mym.expenses.data.local.ExpenseDao
import dev.ridill.mym.expenses.data.local.entity.ExpenseEntity
import dev.ridill.mym.expenses.data.local.relation.ExpenseAndTagRelation
import dev.ridill.mym.expenses.domain.model.Expense
import dev.ridill.mym.expenses.domain.model.ExpenseListItem
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExpenseRepositoryImpl(
    private val dao: ExpenseDao
) : ExpenseRepository {

    override fun getExpenseForDate(monthHyphenYearString: String): Flow<List<ExpenseListItem>> =
        dao.getExpensesForDate(monthHyphenYearString).map { entities ->
            entities.map(ExpenseAndTagRelation::toExpenseListItem)
        }

    override fun getExpenditureForDate(monthHyphenYearString: String): Flow<Double> =
        dao.getExpenditureForDate(monthHyphenYearString)

    override suspend fun insert(expense: Expense) = withContext(Dispatchers.IO) {
        dao.insert(expense.toEntity())
    }

    override suspend fun getExpenseById(id: Long): Expense? = withContext(Dispatchers.IO) {
        dao.getExpenseById(id)?.toExpense()
    }

    override suspend fun delete(expense: Expense) = withContext(Dispatchers.IO) {
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

    override suspend fun deleteMultipleExpenses(ids: List<Long>) = withContext(Dispatchers.IO) {
        dao.deleteMultipleExpenses(ids)
    }

    override suspend fun setTagToExpenses(
        tag: String?,
        expenseIds: List<Long>
    ) = withContext(Dispatchers.IO) {
        dao.setTagToExpenses(tag, expenseIds)
    }
}