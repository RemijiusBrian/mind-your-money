package dev.ridill.mym.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ridill.mym.core.data.db.MYMDatabase
import dev.ridill.mym.expenses.data.local.ExpenseDao
import dev.ridill.mym.expenses.data.local.TagsDao
import dev.ridill.mym.expenses.data.repository.AllExpensesRepositoryImpl
import dev.ridill.mym.expenses.data.repository.ExpenseRepositoryImpl
import dev.ridill.mym.expenses.data.repository.TagsRepositoryImpl
import dev.ridill.mym.expenses.domain.repository.AllExpensesRepository
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import dev.ridill.mym.expenses.domain.repository.TagsRepository

@Module
@InstallIn(SingletonComponent::class)
object ExpenseModule {

    @Provides
    fun provideExpenseDao(database: MYMDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideTagsDao(database: MYMDatabase): TagsDao = database.tagsDao()

    @Provides
    fun provideExpenseRepository(
        dao: ExpenseDao
    ): ExpenseRepository = ExpenseRepositoryImpl(dao)

    @Provides
    fun provideTagsRepository(
        dao: TagsDao
    ): TagsRepository = TagsRepositoryImpl(dao)

    @Provides
    fun provideAllExpensesRepository(
        expenseRepo: ExpenseRepository,
        tagsRepo: TagsRepository
    ): AllExpensesRepository = AllExpensesRepositoryImpl(expenseRepo, tagsRepo)
}