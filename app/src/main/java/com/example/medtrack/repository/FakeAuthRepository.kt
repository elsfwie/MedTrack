package com.example.medtrack.repository

import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepository : AuthRepository {

    private val _currentUser = MutableStateFlow<AuthUser?>(null)

    override val currentUser: StateFlow<AuthUser?> =
        _currentUser.asStateFlow()

    override suspend fun loginCaregiver(
        email: String,
        password: String
    ): Result<AuthUser> {

        // Meniru waktu tunggu layanan agar loading bisa diuji.
        delay(1000)

        val emailMatches =
            email.trim().equals(
                "caregiver@gmail.com",
                ignoreCase = true
            )

        val passwordMatches = password == "MedTrack123"

        if (!emailMatches || !passwordMatches) {
            return Result.failure(
                IllegalArgumentException(
                    "Incorrect email or password."
                )
            )
        }

        val user = AuthUser(
            id = "demo-caregiver-001",
            role = UserRole.CAREGIVER,
            email = "caregiver@gmail.com"
        )

        _currentUser.value = user

        return Result.success(user)
    }

    override suspend fun registerCaregiver(
        email: String,
        password: String
    ): Result<AuthUser> {
        return Result.failure(
            UnsupportedOperationException(
                "Demo registration is not implemented yet."
            )
        )
    }

    override suspend fun loginPatient(
        code: String
    ): Result<AuthUser> {
        return Result.failure(
            UnsupportedOperationException(
                "Demo patient login is not implemented yet."
            )
        )
    }

    override suspend fun logout(): Result<Unit> {
        _currentUser.value = null

        return Result.success(Unit)
    }
}