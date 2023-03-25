package dev.ridill.mym.dashboard.model.repository

import kotlinx.coroutines.flow.Flow

interface DashboardRepository {

    fun getExpenditureForCurrentMonth(): Flow<Double>
}