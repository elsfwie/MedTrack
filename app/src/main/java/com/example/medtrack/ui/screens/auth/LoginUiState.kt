package com.example.medtrack.ui.screens.auth

//untuk kondisi halaman login
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)