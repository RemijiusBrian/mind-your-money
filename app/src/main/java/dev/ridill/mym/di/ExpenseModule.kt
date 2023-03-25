package dev.ridill.mym.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ridill.mym.core.data.db.MYMDatabase
import dev.ridill.mym.core.util.DispatcherProvider
import dev.ridill.mym.expenses.data.local.ExpenseDao
import dev.ridill.mym.expenses.data.local.TagsDao
import dev.ridill.mym.expenses.data.repository.ExpenseRepositoryImpl
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository

@Module
@InstallIn(SingletonComponent::class)
object ExpenseModule {

    @Provides
    fun provideExpenseDao(database: MYMDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideTagsDao(database: MYMDatabase): TagsDao = database.tagsDao()

    @Provides
    fun provideExpenseRepository(
        dao: ExpenseDao,
        dispatcherProvider: DispatcherProvider
    ): ExpenseRepository = ExpenseRepositoryImpl(dao, dispatcherProvider)
}