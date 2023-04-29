package dev.ridill.mym.expenses.domain.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.ridill.mym.R
import dev.ridill.mym.core.notification.NotificationHelper
import dev.ridill.mym.core.util.isPermissionGranted
import dev.ridill.mym.expenses.domain.model.Expense
import kotlin.random.Random

class ExpenseAutoAddNotificationHelper(
    private val context: Context
) : NotificationHelper<Expense> {

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    override fun createNotificationChannelGroup() {
        val group = NotificationChannelGroupCompat
            .Builder(NotificationHelper.ChannelGroups.EXPENSES)
            .setName(context.getString(R.string.notification_channel_group_expenses))
            .setDescription(context.getString(R.string.notification_channel_group_expenses_desc))
            .build()
        notificationManager.createNotificationChannelGroup(group)
    }

    override fun createNotificationChannel() {
        createNotificationChannelGroup()

        val channel = NotificationChannelCompat
            .Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(context.getString(R.string.notification_channel_auto_add_expenses))
            .setGroup(NotificationHelper.ChannelGroups.EXPENSES)
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    override fun getBaseNotification(): NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setGroup(NOTIFICATION_GROUP)

    @SuppressLint("MissingPermission")
    override fun showNotification(vararg data: Expense) {
        if (!isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS)) return
        with(notificationManager) {
            if (!areNotificationsEnabled()) return
            data.forEach { expense ->
                val notification = getBaseNotification()
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .setBigContentTitle(context.getString(R.string.expense_added_from_sms))
                            .bigText(expense.note)
                            .setSummaryText(expense.amount)
                    )
                    .build()
                notify(expense.id.toInt(), notification)
            }

            if (data.size > 1) {
                notify(Random.nextInt(), buildSummaryNotification(data.size))
            }
        }
    }

    private fun buildSummaryNotification(dataSize: Int): Notification =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setGroup(NOTIFICATION_GROUP)
            .setGroupSummary(true)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle("$dataSize Expenses added automatically")
            )
            .build()

    override fun dismissNotification(id: Int) {
        notificationManager.cancel(id)
    }

    override fun dismissAllNotifications() {
        notificationManager.cancelAll()
    }
}

private const val CHANNEL_ID = "mym.notifications.expenses"
private const val NOTIFICATION_GROUP = "EXPENSE_AUTO_ADDED_GROUP"