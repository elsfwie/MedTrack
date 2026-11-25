package com.example.medtrack.ui.screens.patients

data class AddPatientUiState(
    val name: String = "",
    val gender: String = "",
    val age: String = "",
    val emergencyContactName: String = "",
    val countryCode: String = "+62",
    val phoneNumber: String = "",
    val medicalNotes: String = "",
    val nameError: String? = null,
    val genderError: String? = null,
    val ageError: String? = null,
    val emergencyContactNameError: String? = null,
    val phoneError: String? = null,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)