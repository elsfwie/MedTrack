package com.example.medtrack.model

data class AuthUser(
    val id: String,
    val role: UserRole,
    val email: String? = null
)