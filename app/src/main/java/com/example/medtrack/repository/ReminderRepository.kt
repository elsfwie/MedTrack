package com.example.medtrack.repository

import com.example.medtrack.reminder.ReminderCandidate
import com.example.medtrack.reminder.ReminderData
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun observeReminders(): Flow<ReminderData>

    suspend fun getReminder(
        scheduleId: String
    ): ReminderCandidate?
}