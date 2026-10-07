package com.example.medtrack.repository

import androidx.room.withTransaction
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.MedicationSchedule
import com.example.medtrack.model.UserProfile
import com.example.medtrack.model.UserRole
import com.example.medtrack.repository.local.MedTrackDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class LocalDataRepository(
    private val database: MedTrackDatabase
) {
    private val dao = database.medTrackDao()

    private suspend fun ensureInitialData(user: AuthUser) {
        database.withTransaction {
            val initialName = when {
                user.role == UserRole.PATIENT -> "Jessica Jane"
                user.id == "demo_acc_caregiver" -> "John Doe"
                else -> user.email
                    ?.substringBefore("@")
                    ?.takeIf { it.isNotBlank() }
                    ?: "Caregiver"
            }

            val inserted = dao.insertProfile(
                UserProfile(
                    id = user.id,
                    name = initialName,
                    email = user.email.orEmpty(),
                    role = user.role.name
                )
            )

            if (inserted != -1L) {
                val date = LocalDate.now().toString()

                dao.insertSchedules(
                    listOf(
                        MedicationSchedule(
                            ownerId = user.id,
                            id = "demo-0700",
                            date = date,
                            medicineName = "Metformin",
                            dose = "200 mg",
                            time = "07:00",
                            status = "Pending"
                        ),
                        MedicationSchedule(
                            ownerId = user.id,
                            id = "demo-0900",
                            date = date,
                            medicineName = "Metformin",
                            dose = "200 mg",
                            time = "09:00",
                            status = "Pending"
                        )
                    )
                )
            }
        }
    }

    fun observeSchedules(
        user: AuthUser,
        date: LocalDate
    ): Flow<List<MedicationSchedule>> = flow {
        ensureInitialData(user)
        emitAll(dao.observeSchedules(user.id, date.toString()))
    }

    fun observeProfile(user: AuthUser): Flow<UserProfile?> = flow {
        ensureInitialData(user)
        emitAll(dao.observeProfile(user.id))
    }

    suspend fun updateCaregiverProfile(
        userId: String,
        name: String,
        email: String
    ) {
        check(dao.updateCaregiverProfile(userId, name, email) == 1) {
            "Caregiver profile was not found."
        }
    }
}