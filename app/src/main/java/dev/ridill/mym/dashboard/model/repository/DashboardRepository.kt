package dev.ridill.mym.dashboard.model.repository

import dev.ridill.mym.expenses.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {

    fun getExpenditureForCurrentMonth(): Flow<Double>

    fun getExpensesForCurrentMonth(): Flow<List<Expense>>
}