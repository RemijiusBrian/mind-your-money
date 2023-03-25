package dev.ridill.mym.dashboard.data.repository

import dev.ridill.mym.core.util.DateUtil
import dev.ridill.mym.dashboard.model.repository.DashboardRepository
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow

class DashboardRepositoryImpl(
    private val expenseRepo: ExpenseRepository
) : DashboardRepository {

    override fun getExpenditureForCurrentMonth(): Flow<Double> =
        expenseRepo.getExpenditureForDate(
            DateUtil.currentDateTime().format(DateUtil.Formatters.mmHyphenYyyy)
        )
}