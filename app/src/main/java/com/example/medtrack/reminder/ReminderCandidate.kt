package com.example.medtrack.reminder

import com.example.medtrack.model.MedicationSchedule
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

data class ReminderKey(
    val ownerId: String,
    val scheduleId: String
)

data class ReminderCandidate(
    val key: ReminderKey,
    val medicineName: String,
    val dose: String,
    val triggerAtMillis: Long
)

data class ReminderData(
    val candidates: List<ReminderCandidate> = emptyList(),
    val invalidKeys: List<ReminderKey> = emptyList()
)

object ReminderMapper {

    private val timeFormat = DateTimeFormatter
        .ofPattern("HH:mm")
        .withResolverStyle(ResolverStyle.STRICT)

    fun map(
        schedule: MedicationSchedule,
        zoneId: ZoneId
    ): ReminderCandidate? {
        if (
            schedule.status != "Pending" ||
            schedule.ownerId.isBlank() ||
            schedule.id.isBlank() ||
            schedule.medicineName.isBlank() ||
            schedule.dose.isBlank()
        ) {
            return null
        }

        if (!schedule.date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            return null
        }

        if (!schedule.time.matches(Regex("\\d{2}:\\d{2}"))) {
            return null
        }

        return try {
            val dateTime = LocalDateTime.of(
                LocalDate.parse(schedule.date),
                LocalTime.parse(schedule.time, timeFormat)
            )

            val offsets = zoneId.rules.getValidOffsets(dateTime)

            if (offsets.size != 1) {
                null
            } else {
                ReminderCandidate(
                    key = ReminderKey(
                        ownerId = schedule.ownerId,
                        scheduleId = schedule.id
                    ),
                    medicineName = schedule.medicineName,
                    dose = schedule.dose,
                    triggerAtMillis = dateTime
                        .toInstant(offsets.single())
                        .toEpochMilli()
                )
            }
        } catch (_: DateTimeException) {
            null
        }
    }
}