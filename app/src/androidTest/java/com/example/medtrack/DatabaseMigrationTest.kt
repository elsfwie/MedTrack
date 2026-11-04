package com.example.medtrack

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.medtrack.repository.local.DatabaseMigrations
import com.example.medtrack.repository.local.MedTrackDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MedTrackDatabase::class.java
    )

    @Test
    fun migration1To2PreservesProfilesAndSchedules() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
                """
                INSERT INTO profiles (id, name, email, role)
                VALUES (
                    'caregiver-old',
                    'Caregiver Lama',
                    'old@example.test',
                    'CAREGIVER'
                )
                """.trimIndent()
            )

            execSQL(
                """
                INSERT INTO medication_schedules (
                    ownerId, id, date, medicineName, dose, time, status
                )
                VALUES (
                    'caregiver-old',
                    'schedule-old',
                    '2026-11-04',
                    'Obat Uji',
                    '1 tablet',
                    '07:00',
                    'Pending'
                )
                """.trimIndent()
            )

            close()
        }

        val migrated = helper.runMigrationsAndValidate(
            TEST_DB,
            2,
            true,
            DatabaseMigrations.MIGRATION_1_2
        )

        try {
            migrated.query(
                """
                SELECT name, email, role
                FROM profiles
                WHERE id = 'caregiver-old'
                """.trimIndent()
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("Caregiver Lama", cursor.getString(0))
                assertEquals("old@example.test", cursor.getString(1))
                assertEquals("CAREGIVER", cursor.getString(2))
            }

            migrated.query(
                """
                SELECT date, medicineName, dose, time, status
                FROM medication_schedules
                WHERE ownerId = 'caregiver-old'
                    AND id = 'schedule-old'
                """.trimIndent()
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("2026-11-04", cursor.getString(0))
                assertEquals("Obat Uji", cursor.getString(1))
                assertEquals("1 tablet", cursor.getString(2))
                assertEquals("07:00", cursor.getString(3))
                assertEquals("Pending", cursor.getString(4))
            }

            migrated.execSQL(
                """
                INSERT INTO patients (
                    id, createdBy, name, age, emergencyContact
                )
                VALUES (
                    'patient-new',
                    'caregiver-old',
                    'Pasien Baru',
                    60,
                    NULL
                )
                """.trimIndent()
            )

            migrated.query(
                """
                SELECT name, age
                FROM patients
                WHERE id = 'patient-new'
                """.trimIndent()
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("Pasien Baru", cursor.getString(0))
                assertEquals(60, cursor.getInt(1))
            }
        } finally {
            migrated.close()
        }
    }

    companion object {
        private const val TEST_DB = "patient-migration-test.db"
    }
}