package com.example.medtrack.testutil

import com.example.medtrack.model.AuthSession
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.UserRole
import com.example.medtrack.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake AuthRepository khusus unit test LoginViewModel.
 * Data disimpan di memori, tanpa jaringan atau penyimpanan Android.
 */
class TestAuthRepository : AuthRepository {

    val caregiver = AuthUser(
        id = "caregiver-test",
        role = UserRole.CAREGIVER,
        email = "caregiver@gmail.com",
        name = "Caregiver Test"
    )

    val validPassword = "MedTrack123"

    // Saklar untuk mensimulasikan loading dan exception.
    var loginDelayMillis: Long = 0
    var loginException: Exception? = null

    var loginCalls: Int = 0
        private set

    var lastEmail: String? = null
        private set

    var lastPassword: String? = null
        private set

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    override val currentUser: StateFlow<AuthUser?> =
        _currentUser.asStateFlow()

    private val _sessionState =
        MutableStateFlow<AuthSession>(AuthSession.SignedOut)
    override val sessionState: StateFlow<AuthSession> =
        _sessionState.asStateFlow()

    private val _restoreError = MutableStateFlow<String?>(null)
    override val restoreError: StateFlow<String?> =
        _restoreError.asStateFlow()

    override fun restoreSession() {
        publish(_currentUser.value)
    }

    override suspend fun loginCaregiver(
        email: String,
        password: String
    ): Result<AuthUser> {
        loginCalls++
        lastEmail = email
        lastPassword = password

        if (loginDelayMillis > 0) {
            delay(loginDelayMillis)
        }

        loginException?.let { throw it }

        if (
            !email.trim().equals(caregiver.email, ignoreCase = true) ||
            password != validPassword
        ) {
            return Result.failure(
                IllegalArgumentException("Incorrect email or password.")
            )
        }

        publish(caregiver)
        return Result.success(caregiver)
    }

    override suspend fun registerCaregiver(
        name: String,
        email: String,
        password: String
    ): Result<AuthUser> {
        error("Registrasi tidak digunakan dalam test LoginViewModel.")
    }

    override suspend fun loginPatient(
        code: String,
        age: Int,
        deviceId: String
    ): Result<AuthUser> {
        error("Login pasien tidak digunakan dalam test LoginViewModel.")
    }

    override suspend fun logout(): Result<Unit> {
        publish(null)
        return Result.success(Unit)
    }

    private fun publish(user: AuthUser?) {
        _currentUser.value = user
        _sessionState.value = if (user == null) {
            AuthSession.SignedOut
        } else {
            AuthSession.SignedIn(user)
        }
    }
}