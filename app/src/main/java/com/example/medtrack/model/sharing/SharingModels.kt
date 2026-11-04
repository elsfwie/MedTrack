package com.example.medtrack.model.sharing

enum class ConnectionStatus {
    NONE,
    REQUEST_SENT,
    REQUEST_RECEIVED,
    CONNECTED
}

data class CaregiverSearchResult(
    val id: String,
    val name: String,
    val email: String,
    val status: ConnectionStatus
)

data class CaregiverConnection(
    val connectionId: String,
    val caregiverId: String,
    val name: String,
    val email: String
)

data class ConnectionLists(
    val received: List<CaregiverConnection> = emptyList(),
    val sent: List<CaregiverConnection> = emptyList(),
    val connected: List<CaregiverConnection> = emptyList()
)

data class AccessiblePatient(
    val id: String,
    val name: String,
    val age: Int,
    val emergencyContact: String?,
    val sharedBy: String?
)