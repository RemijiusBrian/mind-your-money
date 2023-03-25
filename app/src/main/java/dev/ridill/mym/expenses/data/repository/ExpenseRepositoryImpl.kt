package dev.ridill.mym.expenses.data.repository

import dev.ridill.mym.core.util.DispatcherProvider
import dev.ridill.mym.expenses.data.local.ExpenseDao
import dev.ridill.mym.expenses.data.local.entity.ExpenseAndTagRelation
import dev.ridill.mym.expenses.domain.model.Expense
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExpenseRepositoryImpl(
    private val dao: ExpenseDao,
    private val dispatcher: DispatcherProvider
) : ExpenseRepository {

    override fun getExpenseForDate(monthHyphenYearString: String): Flow<List<Expense>> =
        dao.getExpensesForDate(monthHyphenYearString).map { entities ->
            entities.map(ExpenseAndTagRelation::toExpense)
        }

    override fun getExpenditureForDate(monthHyphenYearString: String): Flow<Double> =
        dao.getExpenditureForDate(monthHyphenYearString)

    override suspend fun insert(expense: Expense) = withContext(dispatcher.io) {
        dao.insert(expense.toEntity())
    }
}