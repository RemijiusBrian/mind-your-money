package dev.ridill.mym.expenses.domain.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import dev.ridill.mym.R
import dev.ridill.mym.application.MYMActivity
import dev.ridill.mym.core.notification.NotificationHelper
import dev.ridill.mym.core.util.isPermissionGranted
import dev.ridill.mym.expenses.domain.model.Expense

@SuppressLint("MissingPermission")
class ExpenseAutoAddNotificationHelper(
    private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val pendingIntentFlags =
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    init {
        init()
    }

    private fun createNotificationChannelGroup() {
        val group = NotificationChannelGroup(
            NotificationHelper.ChannelGroups.EXPENSES,
            context.getString(R.string.notification_channel_group_expenses)
        ).apply {
            description = context.getString(R.string.notification_channel_group_expenses_desc)
        }
        notificationManager.createNotificationChannelGroup(group)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_auto_add_expenses_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            group = NotificationHelper.ChannelGroups.EXPENSES
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun init() {
        createNotificationChannelGroup()
        createNotificationChannel()
    }

    private fun getBaseNotification(): NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setGroup(NOTIFICATION_GROUP)
            .setAutoCancel(true)

    private fun buildPendingIntentWithId(id: Long): PendingIntent {
        val openExpensesIntent = Intent(
            Intent.ACTION_VIEW,
            "https://www.mym.ridill.dev/add_edit_expense/$id".toUri(),
            context,
            MYMActivity::class.java
        )
        return PendingIntent.getActivity(
            context,
            0,
            openExpensesIntent,
            pendingIntentFlags
        )
    }

    fun showNotification(expense: Expense) {
        if (!isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS)) return
        with(notificationManager) {
            if (!areNotificationsEnabled()) return
            val notification = getBaseNotification()
                .setContentTitle(context.getString(R.string.expense_notification_title))
                .setContentText(
                    context.getString(
                        R.string.expense_notification_text,
                        expense.amountFormatted,
                        expense.note
                    )
                )
                .setContentIntent(buildPendingIntentWithId(expense.id))
                .addAction(buildDeleteAction(expense.id))
                .build()
            notify(expense.id.toInt(), notification)
            if (activeNotifications.size > 1)
                notify(SUMMARY_NOTIFICATION_ID, buildSummaryNotification())
        }
    }

    private fun buildDeleteAction(expenseId: Long): NotificationCompat.Action {
        val intent = Intent(context, DeleteExpenseReceiver::class.java).apply {
            action = context.getString(R.string.notification_action_delete_expense)
            putExtra(DELETE_EXPENSE_ID, expenseId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            expenseId.toInt(),
            intent,
            pendingIntentFlags
        )
        return NotificationCompat.Action.Builder(
            R.drawable.ic_launcher_foreground,
            context.getString(R.string.action_delete),
            pendingIntent
        ).build()
    }

    private fun buildSummaryNotification(): Notification = getBaseNotification()
        .setGroup(NOTIFICATION_GROUP)
        .setGroupSummary(true)
        .build()

    fun updateNotificationToDeleted(expenseId: Long) {
        val notification = getBaseNotification()
            .setContentText(context.getString(R.string.expense_deleted))
            .setTimeoutAfter(DELETED_NOTIFICATION_TIMEOUT)
            .build()
        with(notificationManager) {
            notify(expenseId.toInt(), notification)
            if (activeNotifications.size <= 2) {
                cancel(SUMMARY_NOTIFICATION_ID)
            }
        }

    }
}

private const val CHANNEL_ID = "mym.notifications.expenses"
private const val NOTIFICATION_GROUP = "EXPENSE_AUTO_ADDED_GROUP"
private const val SUMMARY_NOTIFICATION_ID = 1_000_000
private const val DELETED_NOTIFICATION_TIMEOUT = 3_000L
const val DELETE_EXPENSE_ID = "DELETE_EXPENSE_ID"