package com.example.medtrack.ui.screens.patients

import com.example.medtrack.model.Patient

data class PatientDetailUiState(
    val isLoading: Boolean = true,
    val patient: Patient? = null,
    val errorMessage: String? = null
)