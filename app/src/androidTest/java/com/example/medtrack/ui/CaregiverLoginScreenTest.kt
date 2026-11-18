package com.example.medtrack.ui

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.medtrack.ui.screens.auth.CaregiverLoginScreen
import com.example.medtrack.ui.screens.auth.LoginUiState
import com.example.medtrack.ui.theme.MedTrackTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI test Compose untuk layar login caregiver (Ch13, UI test screen 1 dari 2).
 * Berjalan di emulator/HP (src/androidTest), tanpa ViewModel dan tanpa repository:
 * CaregiverLoginScreen sudah stateless (menerima LoginUiState + lambda),
 * jadi state dibuat langsung di test dan callback dicatat dengan variabel lokal.
 *
 * Locator memakai teks dan contentDescription yang terlihat/terbaca pengguna.
 */
class CaregiverLoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Kata "Login" muncul dua kali (judul dan tombol); tombol dibedakan
    // karena hanya tombol yang dapat diklik.
    private val tombolLogin: SemanticsMatcher =
        hasText("Login") and hasClickAction()

    // Field dicari sebagai elemen yang dapat diketik (hasSetTextAction)
    // dengan placeholder atau ikon berlabel "Email"/"Password".
    private val fieldEmail: SemanticsMatcher =
        hasSetTextAction() and (hasText("Email") or hasContentDescription("Email"))

    private val fieldPassword: SemanticsMatcher =
        hasSetTextAction() and (hasText("Password") or hasContentDescription("Password"))

    /** Menampilkan layar dengan state tertentu; callback default tidak melakukan apa-apa. */
    private fun showScreen(
        uiState: LoginUiState = LoginUiState(),
        onEmailChanged: (String) -> Unit = {},
        onPasswordChanged: (String) -> Unit = {},
        onLoginClick: () -> Unit = {},
        onBackClick: () -> Unit = {},
        onSignUpClick: () -> Unit = {}
    ) {
        composeRule.setContent {
            MedTrackTheme {
                CaregiverLoginScreen(
                    uiState = uiState,
                    onEmailChanged = onEmailChanged,
                    onPasswordChanged = onPasswordChanged,
                    onLoginClick = onLoginClick,
                    onBackClick = onBackClick,
                    onSignUpClick = onSignUpClick
                )
            }
        }
    }

    // ── Tampilan awal ───────────────────────────────────────────────────────

    @Test
    fun loginScreen_showsEmailPasswordFieldsAndEnabledLoginButton() {
        showScreen()

        composeRule.onNode(fieldEmail).assertIsDisplayed()
        composeRule.onNode(fieldPassword).assertIsDisplayed()
        composeRule.onNode(tombolLogin).assertIsDisplayed().assertIsEnabled()
    }

    // ── Input pengguna diteruskan lewat callback ────────────────────────────

    @Test
    fun  typingEmail_callsOnEmailChangedWithTypedText() {
        var receivedEmail = ""
        showScreen(onEmailChanged = { receivedEmail = it })

        composeRule.onNode(fieldEmail).performTextInput("caregiver@gmail.com")

        assertEquals("caregiver@gmail.com", receivedEmail)
    }

    @Test
    fun typingPassword_callsOnPasswordChangedWithTypedText() {
        var receivedPass = ""
        showScreen(onPasswordChanged = { receivedPass = it })

        composeRule.onNode(fieldPassword).performTextInput("MedTrack123")

        assertEquals("MedTrack123", receivedPass)
    }

    @Test
    fun clickingLoginButton_callsOnLoginClick() {
        var loginClicked = false
        showScreen(
            uiState = LoginUiState(email = "caregiver@gmail.com", password = "MedTrack123"),
            onLoginClick = { loginClicked = true }
        )

        composeRule.onNode(tombolLogin).performClick()

        assertTrue(loginClicked)
    }

    @Test
    fun clickingBackButton_callsOnBackClick() {
        var backClicked = false
        showScreen(onBackClick = { backClicked = true })

        composeRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(backClicked)
    }

    @Test
    fun clickingSignUp_callsOnSignUpClick() {
        var signUpClicked = false
        showScreen(onSignUpClick = { signUpClicked = true })

        composeRule.onNodeWithText("Sign Up").performClick()

        assertTrue(signUpClicked)
    }

    // ── Pesan error dari state ──────────────────────────────────────────────

    @Test
    fun loginFailure_showsErrorMessageFromState() {
        showScreen(
            uiState = LoginUiState(
                email = "caregiver@gmail.com",
                loginError = "Wrong email/password, please try again."
            )
        )

        composeRule
            .onNodeWithText("Wrong email/password, please try again.")
            .assertIsDisplayed()
    }

    @Test
    fun invalidEmail_showsEmailErrorMessage() {
        showScreen(
            uiState = LoginUiState(
                email = "abc",
                emailError = "Please enter a valid email address."
            )
        )

        composeRule
            .onNodeWithText("Please enter a valid email address.")
            .assertIsDisplayed()
    }

    // ── Loading ─────────────────────────────────────────────────────────────

    @Test
    fun whileLoading_buttonShowsLoggingInAndCannotBeClicked() {
        var loginClicked = false
        showScreen(
            uiState = LoginUiState(
                email = "caregiver@gmail.com",
                password = "MedTrack123",
                isLoading = true
            ),
            onLoginClick = { loginClicked = true }
        )

        composeRule
            .onNode(hasText("Logging in...") and hasClickAction())
            .assertIsDisplayed()
            .assertIsNotEnabled()
            .performClick()

        assertFalse(loginClicked)
    }
}