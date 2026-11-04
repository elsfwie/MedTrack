package com.example.medtrack

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.Patient
import com.example.medtrack.model.UserRole
import com.example.medtrack.repository.LocalPatientRepository
import com.example.medtrack.repository.local.DatabaseMigrations
import com.example.medtrack.repository.local.MedTrackDatabase
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
class LocalPatientRepositoryTest {

    private lateinit var context: Context
    private lateinit var database: MedTrackDatabase
    private lateinit var repository: LocalPatientRepository
    private lateinit var session: MutableStateFlow<AuthUser?>

    private val caregiverA = AuthUser(
        id = "caregiver-a",
        role = UserRole.CAREGIVER,
        email = "a@example.test",
        name = "Caregiver A"
    )

    private val caregiverB = AuthUser(
        id = "caregiver-b",
        role = UserRole.CAREGIVER,
        email = "b@example.test",
        name = "Caregiver B"
    )

    @Before
    fun setUp() {
        context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        context.deleteDatabase(TEST_DB)
        session = MutableStateFlow<AuthUser?>(caregiverA)
        database = openDatabase()
        repository = LocalPatientRepository(database, session)
    }

    @After
    fun tearDown() {
        database.close()
        context.deleteDatabase(TEST_DB)
    }

    private fun openDatabase(): MedTrackDatabase {
        return Room.databaseBuilder(
            context,
            MedTrackDatabase::class.java,
            TEST_DB
        )
            .addMigrations(DatabaseMigrations.MIGRATION_1_2)
            .build()
    }

    private suspend fun readPatients(): List<Patient> {
        return withTimeout(5_000) {
            repository.observePatients().first()
        }
    }

    @Test
    fun createReadUpdateDelete() = runBlocking {
        val created = repository.addPatient(
            name = "  Pasien Uji  ",
            age = 60,
            emergencyContact = "  08123456789  "
        )

        assertEquals("Pasien Uji", created.name)
        assertEquals("08123456789", created.emergencyContact)
        assertEquals(caregiverA.id, created.createdBy)
        assertEquals(created, readPatients().single())
        assertEquals(created, repository.getPatient(created.id))

        repository.updatePatient(
            patientId = created.id,
            name = "Pasien Setelah Edit",
            age = 61,
            emergencyContact = " "
        )

        val updated = readPatients().single()

        assertEquals(created.id, updated.id)
        assertEquals("Pasien Setelah Edit", updated.name)
        assertEquals(61, updated.age)
        assertNull(updated.emergencyContact)

        repository.deletePatient(created.id)

        assertTrue(readPatients().isEmpty())
        assertNull(repository.getPatient(created.id))
    }

    @Test
    fun anotherCaregiverCannotReadUpdateOrDeletePatient() = runBlocking {
        val patient = repository.addPatient("Pasien A", 60, null)

        session.value = caregiverB

        assertTrue(readPatients().isEmpty())
        assertNull(repository.getPatient(patient.id))

        val updateError = runCatching {
            repository.updatePatient(
                patient.id,
                "Diubah Akun B",
                70,
                null
            )
        }.exceptionOrNull()

        val deleteError = runCatching {
            repository.deletePatient(patient.id)
        }.exceptionOrNull()

        assertTrue(updateError is IllegalStateException)
        assertTrue(deleteError is IllegalStateException)

        session.value = caregiverA

        assertEquals("Pasien A", readPatients().single().name)
    }

    @Test
    fun rejectsInvalidInputAndUnauthorizedSessions() = runBlocking {
        val emptyNameError = runCatching {
            repository.addPatient("   ", 60, null)
        }.exceptionOrNull()

        val negativeAgeError = runCatching {
            repository.addPatient("Pasien", -1, null)
        }.exceptionOrNull()

        assertTrue(emptyNameError is IllegalArgumentException)
        assertTrue(negativeAgeError is IllegalArgumentException)
        assertTrue(readPatients().isEmpty())

        session.value = null

        val loggedOutError = runCatching {
            repository.addPatient("Pasien", 60, null)
        }.exceptionOrNull()

        assertTrue(loggedOutError is IllegalStateException)
        assertTrue(readPatients().isEmpty())

        session.value = AuthUser(
            id = "patient-account",
            role = UserRole.PATIENT
        )

        val wrongRoleError = runCatching {
            repository.addPatient("Pasien", 60, null)
        }.exceptionOrNull()

        assertTrue(wrongRoleError is IllegalStateException)
        assertTrue(readPatients().isEmpty())
    }

    @Test
    fun patientsRemainAfterDatabaseIsClosedAndReopened() = runBlocking {
        val created = repository.addPatient(
            "Pasien Persisten",
            65,
            null
        )

        database.close()

        database = openDatabase()
        repository = LocalPatientRepository(database, session)

        assertEquals(created, readPatients().single())
    }

    @Test
    fun activeObservationFollowsAccountChangesAndLogout() = runBlocking {
        val created = repository.addPatient("Pasien A", 60, null)
        val emissions = Channel<List<Patient>>(Channel.UNLIMITED)

        val collector = launch {
            repository.observePatients().collect { patients ->
                emissions.send(patients)
            }
        }

        try {
            val first = withTimeout(5_000) {
                emissions.receive()
            }
            assertEquals(created.id, first.single().id)

            session.value = caregiverB

            val second = withTimeout(5_000) {
                emissions.receive()
            }
            assertTrue(second.isEmpty())

            session.value = caregiverA

            val third = withTimeout(5_000) {
                emissions.receive()
            }
            assertEquals(created.id, third.single().id)

            session.value = null

            val fourth = withTimeout(5_000) {
                emissions.receive()
            }
            assertTrue(fourth.isEmpty())
        } finally {
            collector.cancelAndJoin()
            emissions.close()
        }
    }

    companion object {
        private const val TEST_DB = "patient-repository-test.db"
    }
}