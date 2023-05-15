package dev.ridill.mym.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ridill.mym.core.data.db.MYMDatabase
import dev.ridill.mym.core.util.DispatcherProvider
import dev.ridill.mym.expenses.data.local.ExpenseDao
import dev.ridill.mym.expenses.data.local.TagsDao
import dev.ridill.mym.expenses.data.repository.AllExpensesRepositoryImpl
import dev.ridill.mym.expenses.data.repository.ExpenseRepositoryImpl
import dev.ridill.mym.expenses.data.repository.TagsRepositoryImpl
import dev.ridill.mym.expenses.domain.notification.ExpenseAutoAddNotificationHelper
import dev.ridill.mym.expenses.domain.repository.AllExpensesRepository
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import dev.ridill.mym.expenses.domain.repository.TagsRepository
import dev.ridill.mym.expenses.domain.sms.PaymentSmsService

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

    @Provides
    fun provideTagsRepository(
        dao: TagsDao,
        dispatcherProvider: DispatcherProvider
    ): TagsRepository = TagsRepositoryImpl(dao, dispatcherProvider)

    @Provides
    fun provideAllExpensesRepository(
        expenseRepo: ExpenseRepository,
        tagsRepo: TagsRepository
    ): AllExpensesRepository = AllExpensesRepositoryImpl(expenseRepo, tagsRepo)

    @Provides
    fun providePaymentSmsService(): PaymentSmsService = PaymentSmsService()

    @Provides
    fun provideExpenseAutoAddNotificationHelper(
        @ApplicationContext context: Context
    ): ExpenseAutoAddNotificationHelper = ExpenseAutoAddNotificationHelper(context)
}