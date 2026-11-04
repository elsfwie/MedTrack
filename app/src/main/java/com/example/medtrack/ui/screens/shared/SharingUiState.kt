package com.example.medtrack.ui.screens.shared

import com.example.medtrack.model.sharing.AccessiblePatient
import com.example.medtrack.model.sharing.CaregiverSearchResult
import com.example.medtrack.model.sharing.ConnectionLists

enum class SharingTab(val label: String) {
    CAREGIVER("Caregiver"),
    PATIENT("Patients")
}

data class SharingUiState(
    val selectedTab: SharingTab = SharingTab.CAREGIVER,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isSearching: Boolean = false,
    val isActionRunning: Boolean = false,
    val errorMessage: String? = null,
    val searchResults: List<CaregiverSearchResult> = emptyList(),
    val connections: ConnectionLists = ConnectionLists(),
    val patients: List<AccessiblePatient> = emptyList()
)