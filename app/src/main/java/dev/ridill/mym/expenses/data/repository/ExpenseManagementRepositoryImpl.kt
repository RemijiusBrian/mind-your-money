package dev.ridill.mym.expenses.data.repository

import dev.ridill.mym.expenses.domain.model.TagOverview
import dev.ridill.mym.expenses.domain.repository.ExpenseManagementRepository
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import dev.ridill.mym.expenses.domain.repository.TagsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseManagementRepositoryImpl(
    private val expenseRepo: ExpenseRepository,
    private val tagsRepo: TagsRepository
) : ExpenseManagementRepository {

    override fun getYearsList(): Flow<List<String>> = expenseRepo.getDistinctYearsList()
        /*.map { years ->
            // Pad list with future years if size smaller than 10 elements
            years.takeIf { it.size >= 10 } ?: kotlin.run {
                val lastYear = years.lastOrNull() ?: (DateUtil.currentDateTime().year)
                (lastYear..(lastYear + (years.size - 10))).toList()
            }
        }*/.map { years ->
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

    /*override fun getExpenses(tag: String?, monthAndYear: String): Flow<List<Expense>> =
        tagsRepo.getExpensesByTagForDate(tag, monthAndYear).map { entities ->
            entities.map { it.toExpense() }
        }

    override suspend fun tagExpenses(tag: String?, expenseIds: List<Long>) =
        tagsRepo.tagExpenses(tag, expenseIds)

    override suspend fun deleteTag(tag: String) =
        tagsRepo.delete(tag)

    override suspend fun createTag(tag: String, color: Color) =
        tagsRepo.insert(TagInput(tag, color.toArgb()))

    override suspend fun deleteExpenses(ids: List<Long>) =
        expenseRepo.deleteMultipleExpenses(ids)*/
}