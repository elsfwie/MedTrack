package com.example.medtrack.reminder.platform

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.example.medtrack.reminder.ReminderCandidate
import com.example.medtrack.reminder.ReminderKey

class AndroidReminderScheduler(
    context: Context
) : ReminderAlarmPort {

    private val appContext = context.applicationContext

    private val alarmManager =
        appContext.getSystemService(AlarmManager::class.java)

    fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                alarmManager.canScheduleExactAlarms()

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(candidate: ReminderCandidate): Boolean {
        if (!canScheduleExactAlarms()) {
            cancel(candidate.key)
            return false
        }

        val intent = ReminderIntents.fillCandidate(
            ReminderIntents.alarmIntent(
                appContext,
                candidate.key
            ),
            candidate
        )

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = maxOf(
            candidate.triggerAtMillis,
            System.currentTimeMillis() + 1_000L
        )

        return try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
            true
        } catch (_: SecurityException) {
            // Izin bisa berubah di antara pemeriksaan dan pemanggilan API.
            cancel(candidate.key)
            false
        }
    }

    override fun cancel(key: ReminderKey) {
        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            0,
            ReminderIntents.alarmIntent(appContext, key),
            PendingIntent.FLAG_NO_CREATE or
                    PendingIntent.FLAG_IMMUTABLE
        ) ?: return

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}