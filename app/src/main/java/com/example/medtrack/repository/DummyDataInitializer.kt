package com.example.medtrack.repository

import android.content.SharedPreferences
import androidx.room.withTransaction
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.MedicationSchedule
import com.example.medtrack.model.UserProfile
import com.example.medtrack.model.UserRole
import com.example.medtrack.model.demo.PatientDemoData
import com.example.medtrack.repository.local.MedTrackDatabase
import com.example.medtrack.repository.local.PatientEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DummyDataInitializer(
    private val database: MedTrackDatabase,
    private val preferences: SharedPreferences
) {

    suspend fun initialize(user: AuthUser) {
        if (
            user.role != UserRole.CAREGIVER ||
            user.id != "demo_acc_caregiver"
        ) {
            return
        }

        val marker = "dummy_v1_${user.id}"

        if (preferences.getBoolean(marker, false)) {
            return
        }

        val dao = database.medTrackDao()
        val patientDao = database.patientDao()

        val firstTime = LocalDateTime.now()
            .plusMinutes(5)
            .withSecond(0)
            .withNano(0)

        database.withTransaction {
            dao.insertProfile(
                UserProfile(
                    id = user.id,
                    name = user.name ?: "Caregiver Demo",
                    email = user.email.orEmpty(),
                    role = user.role.name
                )
            )

            PatientDemoData.forCaregiver(user.id).forEach { patient ->
                val existing = patientDao.getPatient(
                    caregiverId = user.id,
                    patientId = patient.id
                )

                if (existing == null) {
                    patientDao.insertPatient(
                        PatientEntity(
                            id = patient.id,
                            createdBy = patient.createdBy,
                            name = patient.name,
                            age = patient.age,
                            emergencyContact = patient.emergencyContact,
                            gender = patient.gender,
                            medicalNotes = patient.medicalNotes
                        )
                    )
                }
            }

            val schedules = (1..3).map { number ->
                val scheduledTime = firstTime.plusMinutes(
                    (number - 1) * 5L
                )

                MedicationSchedule(
                    ownerId = user.id,
                    id = "sample-reminder-0$number",
                    date = scheduledTime.toLocalDate().toString(),
                    medicineName = "Obat Contoh $number",
                    dose = "Dosis contoh",
                    time = scheduledTime.format(
                        DateTimeFormatter.ofPattern("HH:mm")
                    ),
                    status = "Pending"
                )
            }

            dao.insertSchedules(schedules)
        }

        withContext(Dispatchers.IO) {
            check(
                preferences.edit()
                    .putBoolean(marker, true)
                    .commit()
            ) {
                "Penanda pengisian dummy gagal disimpan."
            }
        }
    }
}