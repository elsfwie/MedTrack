package com.example.medtrack.reminder.platform

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.medtrack.R
import com.example.medtrack.reminder.ReminderCandidate
import com.example.medtrack.reminder.ReminderKey

class ReminderNotifications(
    context: Context
) : ReminderNotificationPort {

    private val appContext = context.applicationContext

    private val manager =
        appContext.getSystemService(NotificationManager::class.java)

    init {
        createChannel()
    }

    fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pengingat obat",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Pengingat jadwal obat yang tersimpan."
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        manager.createNotificationChannel(channel)
    }

    override fun isEnabled(): Boolean {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        if (
            !NotificationManagerCompat
                .from(appContext)
                .areNotificationsEnabled()
        ) {
            return false
        }

        val channel = manager.getNotificationChannel(CHANNEL_ID)
            ?: return false

        return channel.importance != NotificationManager.IMPORTANCE_NONE
    }

    @SuppressLint("MissingPermission")
    override fun show(candidate: ReminderCandidate): Boolean {
        if (!isEnabled()) {
            return false
        }

        val contentIntent = PendingIntent.getActivity(
            appContext,
            0,
            ReminderIntents.openIntent(appContext, candidate),
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val publicVersion = NotificationCompat.Builder(
            appContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_medication_notification)
            .setContentTitle("MedTrack")
            .setContentText("Ada pengingat jadwal obat.")
            .build()

        val notification = NotificationCompat.Builder(
            appContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_medication_notification)
            .setContentTitle("Waktunya jadwal obat")
            .setContentText(
                "${candidate.medicineName} · ${candidate.dose}"
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "${candidate.medicineName}\n${candidate.dose}"
                    )
            )
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicVersion)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .build()

        return try {
            manager.notify(
                tag(candidate.key),
                NOTIFICATION_ID,
                notification
            )
            true
        } catch (_: SecurityException) {
            false
        }
    }

    override fun cancel(key: ReminderKey) {
        manager.cancel(tag(key), NOTIFICATION_ID)
    }

    private fun tag(key: ReminderKey): String =
        ReminderIntents.uri("notification", key).toString()

    companion object {
        const val CHANNEL_ID = "medication_reminders_v1"

        private const val NOTIFICATION_ID = 1
    }
}