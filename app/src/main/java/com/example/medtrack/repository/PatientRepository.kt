package com.example.medtrack.repository

import com.example.medtrack.model.Patient
import kotlinx.coroutines.flow.Flow

interface PatientRepository {

    fun observePatients(): Flow<List<Patient>>

    suspend fun getPatient(patientId: String): Patient?

    suspend fun addPatient(
        name: String,
        age: Int,
        emergencyContact: String? = null
    ): Patient

    suspend fun updatePatient(
        patientId: String,
        name: String,
        age: Int,
        emergencyContact: String? = null
    )

    suspend fun deletePatient(patientId: String)
}