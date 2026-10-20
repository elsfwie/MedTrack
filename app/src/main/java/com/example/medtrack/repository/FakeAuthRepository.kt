package com.example.medtrack.repository

import com.example.medtrack.model.AuthSession
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.UserRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

// Authentication demo lokal existing; bukan authentication server.
class FakeAuthRepository(
    private val sessionStore: SessionStore,
    private val applicationScope: CoroutineScope
) : AuthRepository {

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
        MutableStateFlow<AuthSession>(AuthSession.Checking)
    override val sessionState: StateFlow<AuthSession> =
        _sessionState.asStateFlow()

    private val _restoreError = MutableStateFlow<String?>(null)

    override val restoreError: StateFlow<String?> = _restoreError.asStateFlow()

    private var restoring = false

    init {
        restoreSession()
    }

    override fun restoreSession() {
        if (restoring || _sessionState.value != AuthSession.Checking) {
            return
        }

        restoring = true
        _restoreError.value = null

        applicationScope.launch {
            try {
                publish(sessionStore.read())
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _restoreError.value =
                    "Unable to restore session. Please try again."
            } finally {
                restoring = false
            }
        }
    }

    private fun normalizeEmail(email: String): String {
        return email.trim().lowercase(Locale.ROOT)
    }

    private fun publish(user: AuthUser?) {
        _currentUser.value = user
        _sessionState.value = if (user == null) {
            AuthSession.SignedOut
        } else {
            AuthSession.SignedIn(user)
        }
    }

    private suspend fun persistSignIn(
        user: AuthUser,
        afterSave: () -> Unit = {}
    ) {
        // Penyimpanan dan publikasi sesi diselesaikan sebagai satu operasi.
        // Pergantian navigation dapat membatalkan ViewModel pemanggil.
        withContext(NonCancellable) {
            sessionStore.save(user)
            afterSave()
            publish(user)
        }
    }

    override suspend fun loginCaregiver(
        email: String,
        password: String
    ): Result<AuthUser> {
        delay(1000)

        val account = accounts[normalizeEmail(email)]

        if (account == null || account.password != password) {
            return Result.failure(
                IllegalArgumentException("Incorrect email or password.")
            )
        }

        persistSignIn(account.user)
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
                IllegalArgumentException("Email is already registered.")
            )
        }

        val user = AuthUser(
            id = UUID.randomUUID().toString(),
            role = UserRole.CAREGIVER,
            email = normalizedEmail
        )

        persistSignIn(user) {
            accounts[normalizedEmail] = DemoAccount(
                user = user,
                password = password
            )
        }

        return Result.success(user)
    }

    override suspend fun loginPatient(code: String): Result<AuthUser> {
        delay(1000)

        if (code != "123456") {
            return Result.failure(
                IllegalArgumentException("Invalid verification code.")
            )
        }

        val user = AuthUser(
            id = "demo-patient-001",
            role = UserRole.PATIENT
        )

        persistSignIn(user)
        return Result.success(user)
    }

    override suspend fun logout(): Result<Unit> {
        withContext(NonCancellable) {
            sessionStore.clear()
            publish(null)
        }

        return Result.success(Unit)
    }
}