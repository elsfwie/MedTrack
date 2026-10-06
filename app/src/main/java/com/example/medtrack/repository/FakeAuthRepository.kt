package com.example.medtrack.repository

import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.UserRole
import com.example.medtrack.model.AuthSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID

// Khusus pengujian lokal dengan akun contoh (demo sebelum connect ke database untuk proses login akun asli).
class FakeAuthRepository : AuthRepository {

    private data class DemoAccount(
        val user: AuthUser,
        val password: String
    )

    private val accounts = mutableMapOf(
        "caregiver@gmail.com" to DemoAccount(
            user = AuthUser(
                id = "demo_acc_caregiver",
                role = UserRole.CAREGIVER,
                email = "caregiver@gmail.com"
            ),
            password = "MedTrack123"
        )
    )

    private val _currentUser = MutableStateFlow<AuthUser?>(null)

    override val currentUser: StateFlow<AuthUser?> =
        _currentUser.asStateFlow()

    private val _sessionState =
        MutableStateFlow<AuthSession>(AuthSession.SignedOut)

    override val sessionState: StateFlow<AuthSession> =
        _sessionState.asStateFlow()

    private fun normalizeEmail(email: String): String {
        return email.trim().lowercase(Locale.ROOT)
    }

    override suspend fun loginCaregiver(
        email: String,
        password: String
    ): Result<AuthUser> {
        delay(1000)

        val account = accounts[normalizeEmail(email)]

        if (account == null || account.password != password) {
            return Result.failure(
                IllegalArgumentException(
                    "Incorrect email or password."
                )
            )
        }

        _currentUser.value = account.user
        _sessionState.value = AuthSession.SignedIn(account.user)

        return Result.success(account.user)
    }

    override suspend fun registerCaregiver(
        email: String,
        password: String
    ): Result<AuthUser> {
        delay(1000)

        val normalizedEmail = normalizeEmail(email)

        if (accounts.containsKey(normalizedEmail)) {
            return Result.failure(
                IllegalArgumentException(
                    "Email is already registered."
                )
            )
        }

        val user = AuthUser(
            id = UUID.randomUUID().toString(),
            role = UserRole.CAREGIVER,
            email = normalizedEmail
        )

        accounts[normalizedEmail] = DemoAccount(
            user = user,
            password = password
        )

        // Mengikuti alur: registrasi berhasil langsung login.
        _currentUser.value = user
        _sessionState.value = AuthSession.SignedIn(user)

        return Result.success(user)
    }

    override suspend fun loginPatient(
        code: String
    ): Result<AuthUser> {
        delay(1000)

        if (code != "123456") {
            return Result.failure(
                IllegalArgumentException(
                    "Invalid verification code."
                )
            )
        }

        val user = AuthUser(
            id = "demo-patient-001",
            role = UserRole.PATIENT
        )

        _currentUser.value = user
        _sessionState.value = AuthSession.SignedIn(user)

        return Result.success(user)
    }

    override suspend fun logout(): Result<Unit> {
        _currentUser.value = null
        _sessionState.value = AuthSession.SignedOut

        return Result.success(Unit)
    }
}