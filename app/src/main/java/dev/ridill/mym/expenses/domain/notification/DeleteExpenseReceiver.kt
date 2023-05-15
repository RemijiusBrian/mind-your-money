package dev.ridill.mym.expenses.domain.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import dev.ridill.mym.R
import dev.ridill.mym.core.navigation.screenSpecs.ARG_INVALID_ID_LONG
import dev.ridill.mym.di.ApplicationScope
import dev.ridill.mym.expenses.domain.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeleteExpenseReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: ExpenseAutoAddNotificationHelper

    @ApplicationScope
    @Inject
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != context.getString(R.string.notification_action_delete_expense)) return

        val expenseId = intent.getLongExtra(DELETE_EXPENSE_ID, ARG_INVALID_ID_LONG)

        applicationScope.launch {
            expenseRepository.deleteById(expenseId)
            notificationHelper.updateNotificationToDeleted(expenseId)
        }
    }
}