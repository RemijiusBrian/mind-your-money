package dev.ridill.mym.settings.domain.back_up

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.ridill.mym.R
import dev.ridill.mym.core.util.Zero

class BackupNotificationManager(
    private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    private fun initChannel() {
        val channel = NotificationChannelCompat
            .Builder(CHANNEL_ID, NotificationManager.IMPORTANCE_LOW)
            .setName(context.getString(R.string.notif_channel_backup))
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    init {
        initChannel()
    }

    fun buildForegroundNotification(
        @StringRes titleRes: Int
    ): Notification = NotificationCompat
        .Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(context.getString(titleRes))
        .setProgress(Int.Zero, Int.Zero, true)
        .build()
}

private const val CHANNEL_ID = "BACKUP_NOTIFICATION"