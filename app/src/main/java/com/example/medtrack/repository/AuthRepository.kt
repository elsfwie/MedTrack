package com.example.medtrack.repository

import com.example.medtrack.model.AuthUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val currentUser: StateFlow<AuthUser?>

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