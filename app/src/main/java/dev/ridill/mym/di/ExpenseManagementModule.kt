package dev.ridill.mym.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ridill.mym.expenses.data.repository.ExpenseManagementRepositoryImpl
import dev.ridill.mym.expenses.domain.repository.ExpenseManagementRepository
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import dev.ridill.mym.expenses.domain.repository.TagsRepository

@Module
@InstallIn(SingletonComponent::class)
object ExpenseManagementModule {

    @Provides
    fun provideExpenseManagementRepository(
        expenseRepo: ExpenseRepository,
        tagsRepo: TagsRepository
    ): ExpenseManagementRepository = ExpenseManagementRepositoryImpl(expenseRepo, tagsRepo)
}