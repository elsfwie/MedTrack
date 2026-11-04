package com.example.medtrack.repository

import androidx.room.withTransaction
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.Patient
import com.example.medtrack.model.UserProfile
import com.example.medtrack.model.UserRole
import com.example.medtrack.repository.local.MedTrackDatabase
import com.example.medtrack.repository.local.PatientEntity
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class LocalPatientRepository(
    private val database: MedTrackDatabase,
    private val currentUser: StateFlow<AuthUser?>
) : PatientRepository {

    private val patientDao = database.patientDao()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePatients(): Flow<List<Patient>> {
        return currentUser.flatMapLatest { user ->
            if (user == null || user.role != UserRole.CAREGIVER) {
                flowOf(emptyList<Patient>())
            } else {
                patientDao.observePatients(user.id).map { entities ->
                    entities.map { entity -> entity.toPatient() }
                }
            }
        }
    }

    override suspend fun getPatient(patientId: String): Patient? {
        val caregiver = requireCaregiver()
        require(patientId.isNotBlank()) {
            "ID pasien tidak boleh kosong."
        }

        return patientDao.getPatient(
            caregiverId = caregiver.id,
            patientId = patientId
        )?.toPatient()
    }

    override suspend fun addPatient(
        name: String,
        age: Int,
        emergencyContact: String?
    ): Patient {
        val caregiver = requireCaregiver()
        val cleanName = validateNameAndAge(name, age)

        val entity = PatientEntity(
            id = UUID.randomUUID().toString(),
            createdBy = caregiver.id,
            name = cleanName,
            age = age,
            emergencyContact = normalizeContact(emergencyContact)
        )

        database.withTransaction {
            // Memastikan parent untuk foreign key tersedia.
            // insertProfile existing memakai IGNORE, sehingga
            // profil yang sudah ada tidak ditimpa.
            database.medTrackDao().insertProfile(
                UserProfile(
                    id = caregiver.id,
                    name = caregiver.name
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() }
                        ?: caregiver.email
                            ?.substringBefore("@")
                            ?.takeIf { it.isNotBlank() }
                        ?: "Caregiver",
                    email = caregiver.email.orEmpty(),
                    role = UserRole.CAREGIVER.name
                )
            )

            patientDao.insertPatient(entity)
        }

        return entity.toPatient()
    }

    override suspend fun updatePatient(
        patientId: String,
        name: String,
        age: Int,
        emergencyContact: String?
    ) {
        val caregiver = requireCaregiver()
        require(patientId.isNotBlank()) {
            "ID pasien tidak boleh kosong."
        }

        val cleanName = validateNameAndAge(name, age)

        val updatedRows = patientDao.updatePatient(
            caregiverId = caregiver.id,
            patientId = patientId,
            name = cleanName,
            age = age,
            emergencyContact = normalizeContact(emergencyContact)
        )

        check(updatedRows == 1) {
            "Pasien tidak ditemukan atau bukan milik caregiver aktif."
        }
    }

    override suspend fun deletePatient(patientId: String) {
        val caregiver = requireCaregiver()
        require(patientId.isNotBlank()) {
            "ID pasien tidak boleh kosong."
        }

        val deletedRows = patientDao.deletePatient(
            caregiverId = caregiver.id,
            patientId = patientId
        )

        check(deletedRows == 1) {
            "Pasien tidak ditemukan atau bukan milik caregiver aktif."
        }
    }

    private fun requireCaregiver(): AuthUser {
        val user = checkNotNull(currentUser.value) {
            "Silakan login terlebih dahulu."
        }

        check(user.role == UserRole.CAREGIVER) {
            "Pengelolaan pasien hanya tersedia untuk caregiver."
        }

        check(user.id.isNotBlank()) {
            "ID caregiver tidak valid."
        }

        return user
    }

    private fun validateNameAndAge(name: String, age: Int): String {
        val cleanName = name.trim()

        require(cleanName.isNotEmpty()) {
            "Nama pasien wajib diisi."
        }

        require(age >= 0) {
            "Umur pasien tidak boleh negatif."
        }

        return cleanName
    }

    private fun normalizeContact(contact: String?): String? {
        return contact?.trim()?.takeIf { it.isNotEmpty() }
    }

    private fun PatientEntity.toPatient(): Patient {
        return Patient(
            id = id,
            createdBy = createdBy,
            name = name,
            age = age,
            emergencyContact = emergencyContact
        )
    }
}