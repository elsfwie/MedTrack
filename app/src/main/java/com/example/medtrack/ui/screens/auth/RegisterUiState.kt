package com.example.medtrack.ui.screens.auth

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val registerError: String? = null,

    val isLoading: Boolean = false,
    val isRegistered: Boolean = false
)