package dev.ridill.mym.core.notification

import androidx.core.app.NotificationCompat

interface NotificationHelper<T> {

    fun createNotificationChannelGroup()

    fun createNotificationChannel()

    fun helperInit() {
        createNotificationChannelGroup()
        createNotificationChannel()
    }

    fun getBaseNotification(): NotificationCompat.Builder

    fun showNotification(vararg data: T)

    fun dismissNotification(id: Int)

    fun dismissAllNotifications()

    object ChannelGroups {
        const val EXPENSES = "NOTIFICATION_CHANNEL_GROUP_EXPENSES"
    }
}