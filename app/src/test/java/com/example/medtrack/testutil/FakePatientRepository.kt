package com.example.medtrack.testutil

import com.example.medtrack.model.Patient
import com.example.medtrack.repository.PatientRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

/**
 * Fake untuk [PatientRepository] (Ch13, Pola A: data diamati lewat Flow).
 *
 * Menggantikan LocalPatientRepository (Room) di unit test:
 * data pasien disimpan di memori (MutableStateFlow), tanpa database,
 * tanpa emulator, dan tanpa login caregiver.
 *
 * Catatan: PatientRepository MedTrack TIDAK mengembalikan Result.
 * Kegagalan ditandai dengan MELEMPAR exception (sama seperti
 * LocalPatientRepository), dan ViewModel menangkapnya dengan try/catch.
 * Karena itu saklar error di fake ini juga melempar exception.
 *
 * Setiap test membuat FakePatientRepository BARU (jangan pakai object/companion),
 * supaya data satu test tidak bocor ke test lain (FIRST: Independent).
 */
class FakePatientRepository(
    initial: List<Patient> = emptyList(),
    private val caregiverId: String = TEST_CAREGIVER_ID
) : PatientRepository {

    private val patients = MutableStateFlow(initial)
    private var nextId = initial.size + 1

    // ── Saklar untuk memaksa kondisi yang sulit dibuat di dunia nyata ──────────

    /** Jika diisi, observePatients() gagal (mis. database rusak / query error). */
    var observeError: Exception? = null

    /** Jika diisi, addPatient/updatePatient/deletePatient melempar exception ini. */
    var writeError: Exception? = null

    /** > 0 agar state loading (isSaving) sempat diamati. Memakai jam virtual runTest. */
    var writeDelayMillis: Long = 0

    // ── Untuk assertion (state-based) ──────────────────────────────────────────

    /** Isi "database" saat ini. Contoh: assertTrue(repo.current.isEmpty()) */
    val current: List<Patient> get() = patients.value

    /** Berapa kali addPatient berhasil dipanggil. Contoh: assertEquals(0, repo.addCalls) */
    var addCalls: Int = 0
        private set

    // ── Implementasi interface PatientRepository ───────────────────────────────

    override fun observePatients(): Flow<List<Patient>> {
        observeError?.let { error -> return flow { throw error } }
        return patients
    }

    override suspend fun getPatient(patientId: String): Patient? =
        patients.value.firstOrNull { it.id == patientId }

    override suspend fun addPatient(
        name: String,
        age: Int,
        emergencyContact: String?,
        gender: String,
        medicalNotes: String
    ): Patient {
        simulateWrite()

        val patient = Patient(
            id = "patient-${nextId++}",
            createdBy = caregiverId,
            name = name.trim(),
            age = age,
            emergencyContact = emergencyContact?.trim()?.takeIf { it.isNotEmpty() },
            gender = gender,
            medicalNotes = medicalNotes.trim()
        )
        patients.update { it + patient }
        addCalls++
        return patient
    }

    override suspend fun updatePatient(
        patientId: String,
        name: String,
        age: Int,
        emergencyContact: String?,
        gender: String,
        medicalNotes: String
    ) {
        simulateWrite()
        check(patients.value.any { it.id == patientId }) {
            "Pasien tidak ditemukan atau bukan milik caregiver aktif."
        }

        patients.update { list ->
            list.map { patient ->
                if (patient.id == patientId) {
                    patient.copy(
                        name = name.trim(),
                        age = age,
                        emergencyContact = emergencyContact?.trim()?.takeIf { it.isNotEmpty() },
                        gender = gender,
                        medicalNotes = medicalNotes.trim()
                    )
                } else {
                    patient
                }
            }
        }
    }

    override suspend fun deletePatient(patientId: String) {
        simulateWrite()
        check(patients.value.any { it.id == patientId }) {
            "Pasien tidak ditemukan atau bukan milik caregiver aktif."
        }

        patients.update { list -> list.filterNot { it.id == patientId } }
    }

    private suspend fun simulateWrite() {
        if (writeDelayMillis > 0) delay(writeDelayMillis)
        writeError?.let { throw it }
    }
}
