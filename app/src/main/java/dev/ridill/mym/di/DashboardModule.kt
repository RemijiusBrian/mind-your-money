package dev.ridill.mym.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ridill.mym.dashboard.data.repository.DashboardRepositoryImpl
import dev.ridill.mym.dashboard.model.repository.DashboardRepository
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

    @Provides
    fun provideDashboardRepository(
        expenseRepository: ExpenseRepository
    ): DashboardRepository = DashboardRepositoryImpl(expenseRepository)
}