package com.example.medtrack.ui.screens.auth

import com.example.medtrack.model.AuthSession
import com.example.medtrack.testutil.MainDispatcherRule
import com.example.medtrack.testutil.TestAuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule(StandardTestDispatcher())

    // JUnit membuat instance baru untuk setiap test.
    private val repository = TestAuthRepository()

    private fun createValidViewModel(): LoginViewModel {
        return LoginViewModel(repository).apply {
            onEmailChanged("caregiver@gmail.com")
            onPasswordChanged(repository.validPassword)
        }
    }

    @Test
    fun `login sukses merapikan email menghapus password dan memperbarui state`() =
        runTest {
            val viewModel = createValidViewModel()
            viewModel.onEmailChanged("  caregiver@gmail.com  ")

            viewModel.login()
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertTrue(state.isLoggedIn)
            assertFalse(state.isLoading)
            assertEquals("caregiver@gmail.com", state.email)
            assertEquals("", state.password)
            assertNull(state.emailError)
            assertNull(state.passwordError)
            assertNull(state.loginError)

            assertEquals("caregiver@gmail.com", repository.lastEmail)
            assertEquals(repository.validPassword, repository.lastPassword)
            assertEquals(1, repository.loginCalls)
            assertEquals(repository.caregiver, repository.currentUser.value)
            assertEquals(
                AuthSession.SignedIn(repository.caregiver),
                repository.sessionState.value
            )
        }

    @Test
    fun `password salah menampilkan pesan gagal login`() = runTest {
        val viewModel = createValidViewModel()
        viewModel.onPasswordChanged("PasswordSalah")

        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoggedIn)
        assertFalse(state.isLoading)
        assertEquals(
            "Wrong email/password, please try again.",
            state.loginError
        )
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertEquals(1, repository.loginCalls)
        assertNull(repository.currentUser.value)
        assertEquals(
            AuthSession.SignedOut,
            repository.sessionState.value
        )
    }

    @Test
    fun `exception repository menampilkan pesan umum dan menghentikan loading`() =
        runTest {
            repository.loginException = IOException("Koneksi terputus")
            val viewModel = createValidViewModel()

            viewModel.login()
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoggedIn)
            assertFalse(state.isLoading)
            assertEquals(
                "Unable to log in. Please try again.",
                state.loginError
            )
            assertEquals(1, repository.loginCalls)
            assertNull(repository.currentUser.value)
        }

    @Test
    fun `input kosong ditolak tanpa memanggil repository`() = runTest {
        val viewModel = LoginViewModel(repository)

        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals("Email is required.", state.emailError)
        assertEquals("Password is required.", state.passwordError)
        assertNull(state.loginError)
        assertFalse(state.isLoggedIn)
        assertFalse(state.isLoading)
        assertEquals(0, repository.loginCalls)
    }

    @Test
    fun `format email salah ditolak tanpa memanggil repository`() = runTest {
        val viewModel = createValidViewModel()
        viewModel.onEmailChanged("email-tidak-valid")

        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(
            "Please enter a valid email address.",
            state.emailError
        )
        assertNull(state.passwordError)
        assertNull(state.loginError)
        assertFalse(state.isLoggedIn)
        assertFalse(state.isLoading)
        assertEquals(0, repository.loginCalls)
    }

    @Test
    fun `loading aktif selama proses dan login berulang diabaikan`() = runTest {
        repository.loginDelayMillis = 1_000
        val viewModel = createValidViewModel()

        viewModel.login()
        runCurrent()

        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isLoggedIn)
        assertEquals(1, repository.loginCalls)

        // Tekanan tombol kedua saat proses masih berjalan.
        viewModel.login()
        runCurrent()

        assertTrue(viewModel.uiState.value.isLoading)
        assertEquals(1, repository.loginCalls)

        // Menyelesaikan delay menggunakan waktu virtual.
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isLoggedIn)
        assertNull(viewModel.uiState.value.loginError)
        assertEquals(1, repository.loginCalls)
    }

    @Test
    fun `login dapat dicoba lagi setelah password salah`() = runTest {
        val viewModel = createValidViewModel()
        viewModel.onPasswordChanged("PasswordSalah")

        viewModel.login()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoggedIn)
        assertEquals(
            "Wrong email/password, please try again.",
            viewModel.uiState.value.loginError
        )

        viewModel.onPasswordChanged(repository.validPassword)

        assertNull(viewModel.uiState.value.loginError)

        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertTrue(state.isLoggedIn)
        assertFalse(state.isLoading)
        assertNull(state.loginError)
        assertEquals("", state.password)
        assertEquals(2, repository.loginCalls)
        assertEquals(repository.caregiver, repository.currentUser.value)
    }

    @Test
    fun `memperbaiki input menghapus pesan validasi pada field terkait`() =
        runTest {
            val viewModel = LoginViewModel(repository)

            viewModel.login()
            advanceUntilIdle()

            assertEquals(
                "Email is required.",
                viewModel.uiState.value.emailError
            )
            assertEquals(
                "Password is required.",
                viewModel.uiState.value.passwordError
            )

            viewModel.onEmailChanged("caregiver@gmail.com")

            assertEquals(
                "caregiver@gmail.com",
                viewModel.uiState.value.email
            )
            assertNull(viewModel.uiState.value.emailError)
            assertEquals(
                "Password is required.",
                viewModel.uiState.value.passwordError
            )

            viewModel.onPasswordChanged(repository.validPassword)

            assertEquals(
                repository.validPassword,
                viewModel.uiState.value.password
            )
            assertNull(viewModel.uiState.value.passwordError)
            assertNull(viewModel.uiState.value.loginError)
            assertFalse(viewModel.uiState.value.isLoggedIn)
            assertEquals(0, repository.loginCalls)
        }
}