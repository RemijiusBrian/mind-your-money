package dev.ridill.mym.dashboard.presentation

import dev.ridill.mym.core.util.Zero
import dev.ridill.mym.expenses.domain.model.ExpenseListItem

data class DashboardState(
    val expenditure: Double = Double.Zero,
    val isMonthlyLimitSet: Boolean = false,
    val monthlyLimit: Long = Long.Zero,
    val balanceFromLimit: Double = Double.Zero,
    val balancePercent: Float = Float.Zero,
    val expenses: List<ExpenseListItem> = emptyList(),
    val username: String? = null
)