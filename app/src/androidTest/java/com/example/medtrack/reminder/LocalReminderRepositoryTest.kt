package com.example.medtrack.reminder

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.MedicationSchedule
import com.example.medtrack.model.UserProfile
import com.example.medtrack.model.UserRole
import com.example.medtrack.repository.LocalReminderRepository
import com.example.medtrack.repository.local.MedTrackDatabase
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalReminderRepositoryTest {

    private lateinit var database: MedTrackDatabase
    private lateinit var repository: LocalReminderRepository

    private val user = AuthUser(
        id = "reminder-owner-a",
        role = UserRole.CAREGIVER
    )

    private val currentUser =
        MutableStateFlow<AuthUser?>(user)

    @Before
    fun setUp() {
        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            MedTrackDatabase::class.java
        ).build()

        repository = LocalReminderRepository(
            database = database,
            currentUser = currentUser,
            zoneProvider = {
                ZoneId.of("Asia/Jakarta")
            }
        )

        runBlocking {
            database.medTrackDao().insertProfile(
                UserProfile(
                    id = user.id,
                    name = "Pengguna Uji",
                    email = "test@example.test",
                    role = user.role.name
                )
            )
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun schedule(
        id: String = "schedule-1",
        time: String = "08:00",
        status: String = "Pending"
    ) = MedicationSchedule(
        ownerId = user.id,
        id = id,
        date = "2030-01-01",
        medicineName = "Obat Contoh",
        dose = "Dosis contoh",
        time = time,
        status = status
    )

    private suspend fun read(): ReminderData =
        withTimeout(5_000L) {
            repository.observeReminders().first()
        }

    @Test
    fun onlyPendingValidSchedulesBecomeCandidates() = runBlocking {
        database.medTrackDao().insertSchedule(
            schedule(id = "pending")
        )
        database.medTrackDao().insertSchedule(
            schedule(id = "taken", status = "Taken")
        )
        database.medTrackDao().insertSchedule(
            schedule(id = "invalid", time = "25:99")
        )

        val result = read()

        assertEquals(
            listOf("pending"),
            result.candidates.map { it.key.scheduleId }
        )
        assertEquals(
            listOf("invalid"),
            result.invalidKeys.map { it.scheduleId }
        )
    }

    @Test
    fun deletedScheduleCannotBeFetchedForDelivery() = runBlocking {
        val schedule = schedule()

        database.medTrackDao().insertSchedule(schedule)

        assertEquals(
            schedule.id,
            repository.getReminder(schedule.id)?.key?.scheduleId
        )

        database.medTrackDao().deleteSchedule(
            schedule.ownerId,
            schedule.id
        )

        assertNull(repository.getReminder(schedule.id))
    }

    @Test
    fun logoutReturnsNoCandidates() = runBlocking {
        database.medTrackDao().insertSchedule(schedule())

        assertEquals(1, read().candidates.size)

        currentUser.value = null

        assertTrue(read().candidates.isEmpty())
        assertNull(repository.getReminder("schedule-1"))
    }

    @Test
    fun anotherOwnerCannotReadSchedule() = runBlocking {
        database.medTrackDao().insertSchedule(schedule())

        currentUser.value = user.copy(
            id = "reminder-owner-b"
        )

        assertTrue(read().candidates.isEmpty())
        assertNull(repository.getReminder("schedule-1"))
    }
}