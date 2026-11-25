package com.example.medtrack.reminder.platform

import com.example.medtrack.reminder.ReminderCandidate
import com.example.medtrack.reminder.ReminderKey

data class StoredReminder(
    val candidate: ReminderCandidate,
    val handled: Boolean = false
)

interface ReminderAlarmPort {
    fun schedule(candidate: ReminderCandidate): Boolean

    fun cancel(key: ReminderKey)
}

interface ReminderNotificationPort {
    fun isEnabled(): Boolean

    fun show(candidate: ReminderCandidate): Boolean

    fun cancel(key: ReminderKey)
}

interface ReminderStateStore {
    fun read(): Map<ReminderKey, StoredReminder>

    fun write(records: Map<ReminderKey, StoredReminder>)
}

object ReminderPolicy {
    const val MAX_LATENESS_MILLIS = 10 * 60 * 1000L

    fun canRecover(
        candidate: ReminderCandidate,
        previous: StoredReminder?,
        nowMillis: Long
    ): Boolean {
        if (candidate.triggerAtMillis > nowMillis) {
            return true
        }

        val sameOccurrence =
            previous?.candidate?.triggerAtMillis ==
                    candidate.triggerAtMillis

        return sameOccurrence &&
                previous?.handled == false &&
                nowMillis - candidate.triggerAtMillis <= MAX_LATENESS_MILLIS
    }
}