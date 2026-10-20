package com.example.medtrack.repository

import com.example.medtrack.model.AuthSession
import com.example.medtrack.model.AuthUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val currentUser: StateFlow<AuthUser?>
    val sessionState: StateFlow<AuthSession>
    val restoreError: StateFlow<String?>

    fun restoreSession()

    suspend fun loginCaregiver(
        email: String,
        password: String
    ): Result<AuthUser>

    suspend fun registerCaregiver(
        email: String,
        password: String
    ): Result<AuthUser>

    suspend fun loginPatient(
        code: String
    ): Result<AuthUser>

    suspend fun logout(): Result<Unit>
}