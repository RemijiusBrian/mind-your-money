package dev.ridill.mym.core.notification

import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat

interface NotificationHelper {

    fun createNotificationChannelGroup(
        id: String,
        @StringRes nameRes: Int,
        @StringRes descriptionRes: Int
    )

    fun createNotificationChannel()

    fun getBaseNotification(): NotificationCompat.Builder

    fun showNotification(vararg notifications: NotificationCompat.Builder)

    fun updateNotification(
        update: (updateNotification: NotificationCompat.Builder) -> NotificationCompat.Builder
    )

    fun dismissNotification(id: Int)

    fun dismissAllNotifications()

    object ChannelGroups {
        const val BILLS = "NOTIFICATION_CHANNEL_GROUP_BILLS"
        const val EXPENSES = "NOTIFICATION_CHANNEL_GROUP_EXPENSES"
    }
}