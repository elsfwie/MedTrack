package com.example.medtrack.ui.screens.auth

data class PatientLoginUiState(
    val code: String = "",
    val codeError: String? = null,
    val loginError: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)