package com.example.medtrack.reminder.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.medtrack.MainActivity
import com.example.medtrack.reminder.ReminderCandidate
import com.example.medtrack.reminder.ReminderKey

data class ReminderOpenRequest(
    val key: ReminderKey,
    val triggerAtMillis: Long
)

object ReminderIntents {
    const val ACTION_FIRE =
        "com.example.medtrack.action.FIRE_REMINDER"

    const val ACTION_OPEN =
        "com.example.medtrack.action.OPEN_REMINDER"

    const val EXTRA_OWNER_ID = "reminder_owner_id"
    const val EXTRA_SCHEDULE_ID = "reminder_schedule_id"
    const val EXTRA_TRIGGER_AT = "reminder_trigger_at"

    fun uri(kind: String, key: ReminderKey): Uri =
        Uri.Builder()
            .scheme("medtrack")
            .authority(kind)
            .appendPath(key.ownerId)
            .appendPath(key.scheduleId)
            .build()

    fun alarmIntent(
        context: Context,
        key: ReminderKey
    ): Intent =
        Intent(context, ReminderAlarmReceiver::class.java)
            .setAction(ACTION_FIRE)
            .setData(uri("alarm", key))

    fun fillCandidate(
        intent: Intent,
        candidate: ReminderCandidate
    ): Intent =
        intent
            .putExtra(EXTRA_OWNER_ID, candidate.key.ownerId)
            .putExtra(EXTRA_SCHEDULE_ID, candidate.key.scheduleId)
            .putExtra(EXTRA_TRIGGER_AT, candidate.triggerAtMillis)

    fun openIntent(
        context: Context,
        candidate: ReminderCandidate
    ): Intent =
        fillCandidate(
            Intent(context, MainActivity::class.java)
                .setAction(ACTION_OPEN)
                .setData(uri("open", candidate.key))
                .addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                ),
            candidate
        )

    fun readRequest(intent: Intent): ReminderOpenRequest? {
        val ownerId = intent.getStringExtra(EXTRA_OWNER_ID)
            ?.takeIf { it.isNotBlank() }
            ?: return null

        val scheduleId = intent.getStringExtra(EXTRA_SCHEDULE_ID)
            ?.takeIf { it.isNotBlank() }
            ?: return null

        val triggerAtMillis =
            intent.getLongExtra(EXTRA_TRIGGER_AT, -1L)

        if (triggerAtMillis <= 0L) {
            return null
        }

        return ReminderOpenRequest(
            key = ReminderKey(ownerId, scheduleId),
            triggerAtMillis = triggerAtMillis
        )
    }

    fun readOpenRequest(intent: Intent?): ReminderOpenRequest? {
        if (intent == null || intent.action != ACTION_OPEN) {
            return null
        }

        val request = readRequest(intent) ?: return null

        if (intent.data != uri("open", request.key)) {
            return null
        }

        return request
    }
}