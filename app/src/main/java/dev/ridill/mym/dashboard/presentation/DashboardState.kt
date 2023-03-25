package dev.ridill.mym.dashboard.presentation

import dev.ridill.mym.core.util.Zero

data class DashboardState(
    val expenditure: Double = Double.Zero,
    val isMonthlyLimitSet: Boolean = false,
    val monthlyLimit: Long = Long.Zero,
    val balanceFromLimit: Double = Double.Zero,
    val balancePercent: Float = Float.Zero
)