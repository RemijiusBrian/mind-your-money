package dev.ridill.mym.expenses.data.repository

import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.expenses.domain.model.Expense
import dev.ridill.mym.expenses.domain.model.TagInput
import dev.ridill.mym.expenses.domain.model.TagOverview
import dev.ridill.mym.expenses.domain.repository.AllExpensesRepository
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import dev.ridill.mym.expenses.domain.repository.TagsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AllExpensesRepositoryImpl(
    private val expenseRepo: ExpenseRepository,
    private val tagsRepo: TagsRepository
) : AllExpensesRepository {

    override fun getYearsList(): Flow<List<String>> = expenseRepo.getDistinctYearsList()
        .map { years ->
            // Pad list to show a min number of elements in years list
            val paddingDifference = YEARS_LIST_PADDING - years.size
            val lastItem = years.lastOrNull()?.plus(1) ?: DateUtil.currentDateTime().year
            val paddingElements = (lastItem until (lastItem + paddingDifference.coerceAtLeast(0)))
                .toList()
            years + paddingElements
        }.map { years ->
            years.map { it.toString() }
        }

    override fun getTotalExpenditure(monthAndYear: String): Flow<Double> =
        expenseRepo.getExpenditureForDate(monthAndYear)

    override fun getTagOverviews(
        totalExpenditure: Double,
        monthAndYear: String
    ): Flow<List<TagOverview>> = tagsRepo.getTagWithExpenditure(monthAndYear).map { relations ->
        relations.map { it.toTagOverview(totalExpenditure) }
    }

    override suspend fun deleteTag(tag: String) = tagsRepo.delete(tag)

    override fun getExpensesByTagForDate(tag: String?, monthAndYear: String): Flow<List<Expense>> =
        expenseRepo.getExpenseByTagForDate(tag, monthAndYear)

    override suspend fun tagExpenses(tag: String?, expenseIds: List<Long>) =
        expenseRepo.setTagToExpenses(tag, expenseIds)

    override suspend fun createTag(input: TagInput) =
        tagsRepo.insert(input)

    override suspend fun deleteExpenses(ids: List<Long>) =
        expenseRepo.deleteMultipleExpenses(ids)
}

private const val YEARS_LIST_PADDING = 10