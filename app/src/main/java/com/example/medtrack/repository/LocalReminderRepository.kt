package com.example.medtrack.repository

import com.example.medtrack.model.AuthUser
import com.example.medtrack.reminder.ReminderCandidate
import com.example.medtrack.reminder.ReminderData
import com.example.medtrack.reminder.ReminderKey
import com.example.medtrack.reminder.ReminderMapper
import com.example.medtrack.repository.local.MedTrackDatabase
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class LocalReminderRepository(
    database: MedTrackDatabase,
    private val currentUser: StateFlow<AuthUser?>,
    private val zoneProvider: () -> ZoneId = {
        ZoneId.systemDefault()
    }
) : ReminderRepository {

    private val dao = database.medTrackDao()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeReminders(): Flow<ReminderData> {
        return currentUser.flatMapLatest { user ->
            if (user == null || user.id.isBlank()) {
                flowOf(ReminderData())
            } else {
                dao.observePendingSchedules(user.id).map { schedules ->
                    val zone = zoneProvider()
                    val candidates = mutableListOf<ReminderCandidate>()
                    val invalidKeys = mutableListOf<ReminderKey>()

                    schedules.forEach { schedule ->
                        val candidate = ReminderMapper.map(schedule, zone)

                        if (candidate != null) {
                            candidates += candidate
                        } else {
                            invalidKeys += ReminderKey(
                                ownerId = schedule.ownerId,
                                scheduleId = schedule.id
                            )
                        }
                    }

                    ReminderData(
                        candidates = candidates.sortedBy {
                            it.triggerAtMillis
                        },
                        invalidKeys = invalidKeys
                    )
                }
            }
        }.distinctUntilChanged()
    }

    override suspend fun getReminder(
        scheduleId: String
    ): ReminderCandidate? {
        require(scheduleId.isNotBlank())

        val user = currentUser.value ?: return null
        if (user.id.isBlank()) return null

        val schedule = dao.getSchedule(
            ownerId = user.id,
            scheduleId = scheduleId
        ) ?: return null

        if (currentUser.value != user) return null

        return ReminderMapper.map(
            schedule = schedule,
            zoneId = zoneProvider()
        )
    }
}