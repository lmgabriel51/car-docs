package com.example.cardocs.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.cardocs.MainActivity
import com.example.cardocs.R
import com.example.cardocs.data.DocumentType

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "car_expiry_channel"
        const val CHANNEL_NAME = "Car Document Expiration"
        const val CHANNEL_DESCRIPTION = "Notifications for expiring car documents"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendExpirationNotification(
        documentType: String,
        carName: String,
        daysRemaining: Int,
        notificationId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val title = when {
            daysRemaining == 0 -> context.getString(R.string.notification_title_today, documentType)
            daysRemaining == 1 -> context.getString(R.string.notification_title_tomorrow, documentType)
            else -> context.getString(R.string.notification_title_soon, documentType)
        }

        val message = when {
            daysRemaining == 0 -> context.getString(R.string.notification_message_today, carName, documentType)
            daysRemaining == 1 -> context.getString(R.string.notification_message_tomorrow, carName, documentType)
            else -> context.getString(R.string.notification_message_days, carName, documentType, daysRemaining)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
