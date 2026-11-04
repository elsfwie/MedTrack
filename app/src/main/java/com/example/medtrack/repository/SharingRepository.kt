package com.example.medtrack.repository

import com.example.medtrack.model.sharing.AccessiblePatient
import com.example.medtrack.model.sharing.CaregiverSearchResult
import com.example.medtrack.model.sharing.ConnectionLists

interface SharingRepository {
    suspend fun searchCaregivers(
        query: String
    ): List<CaregiverSearchResult>

    suspend fun getConnections(): ConnectionLists

    suspend fun getAccessiblePatients(): List<AccessiblePatient>

    suspend fun sendConnectionRequest(
        caregiverId: String
    )

    suspend fun acceptConnectionRequest(
        connectionId: String
    )

    suspend fun declineConnectionRequest(
        connectionId: String
    )

    suspend fun sharePatient(
        patientId: String,
        caregiverId: String
    )
}