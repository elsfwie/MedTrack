package com.example.medtrack.repository

import com.example.medtrack.model.Patient
import kotlinx.coroutines.flow.Flow

interface PatientRepository {

    fun observePatients(): Flow<List<Patient>>

    suspend fun getPatient(patientId: String): Patient?

    suspend fun addPatient(
        name: String,
        age: Int,
        emergencyContact: String? = null,
        gender: String = "Not specified",
        medicalNotes: String = ""
    ): Patient

    suspend fun updatePatient(
        patientId: String,
        name: String,
        age: Int,
        emergencyContact: String? = null,
        gender: String = "Not specified",
        medicalNotes: String = ""
    )

    suspend fun deletePatient(patientId: String)
}