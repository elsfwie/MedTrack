package com.example.medtrack.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medtrack.repository.AuthRepository
import com.example.medtrack.validation.AuthValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//repository contract

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        if (_uiState.value.isLoading || _uiState.value.isLoggedIn) {
            return
        }

        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                loginError = null
            )
        }
    }

    fun onPasswordChanged(password: String) {
        if (_uiState.value.isLoading || _uiState.value.isLoggedIn) {
            return
        }

        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                loginError = null
            )
        }
    }

    fun login() {
        val currentState = _uiState.value

        // mencegah pengiriman login berulang
        if (currentState.isLoading || currentState.isLoggedIn) {
            return
        }

        val email = currentState.email.trim()
        val password = currentState.password

        val emailError = AuthValidator.validateEmail(email)
        val passwordError =
            AuthValidator.validateLoginPassword(password)

        _uiState.update {
            it.copy(
                email = email,
                emailError = emailError,
                passwordError = passwordError,
                loginError = null
            )
        }

        // validasi input sebelum diproses repository
        if (emailError != null || passwordError != null) {
            return
        }

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val result = authRepository.loginCaregiver(
                    email = email,
                    password = password
                )

                // cancelation login yang bukan berasal dari failed login
                val failure = result.exceptionOrNull()
                if (failure is CancellationException) {
                    throw failure
                }

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            password = "",
                            isLoggedIn = true,
                            loginError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            loginError =
                                "Unable to log in. Please try again."
                        )
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        loginError =
                            "Unable to log in. Please try again."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}