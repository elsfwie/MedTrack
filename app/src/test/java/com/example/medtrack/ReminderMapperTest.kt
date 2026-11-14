package com.example.medtrack

import com.example.medtrack.model.MedicationSchedule
import com.example.medtrack.reminder.ReminderMapper
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReminderMapperTest {

    private val schedule = MedicationSchedule(
        ownerId = "caregiver-test",
        id = "schedule-test",
        date = "2030-01-01",
        medicineName = "Obat Contoh",
        dose = "Dosis contoh",
        time = "07:05",
        status = "Pending"
    )

    @Test
    fun convertsJakartaTimeToCorrectInstant() {
        val candidate = requireNotNull(
            ReminderMapper.map(
                schedule,
                ZoneId.of("Asia/Jakarta")
            )
        )

        assertEquals(
            Instant.parse("2030-01-01T00:05:00Z").toEpochMilli(),
            candidate.triggerAtMillis
        )

        assertEquals("caregiver-test", candidate.key.ownerId)
        assertEquals("schedule-test", candidate.key.scheduleId)
    }

    @Test
    fun rejectsInvalidDateAndTime() {
        val zone = ZoneId.of("Asia/Jakarta")

        assertNull(
            ReminderMapper.map(
                schedule.copy(date = "2030-02-30"),
                zone
            )
        )

        assertNull(
            ReminderMapper.map(
                schedule.copy(time = "25:99"),
                zone
            )
        )
    }

    @Test
    fun excludesNonPendingSchedule() {
        assertNull(
            ReminderMapper.map(
                schedule.copy(status = "Taken"),
                ZoneId.of("Asia/Jakarta")
            )
        )
    }

    @Test
    fun rejectsAmbiguousLocalTime() {
        assertNull(
            ReminderMapper.map(
                schedule.copy(
                    date = "2025-11-02",
                    time = "01:30"
                ),
                ZoneId.of("America/New_York")
            )
        )
    }
}