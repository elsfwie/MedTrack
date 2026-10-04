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

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        if (_uiState.value.isLoading || _uiState.value.isRegistered) {
            return
        }

        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                registerError = null
            )
        }
    }

    fun onPasswordChanged(password: String) {
        if (_uiState.value.isLoading || _uiState.value.isRegistered) {
            return
        }

        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                confirmPasswordError = null,
                registerError = null
            )
        }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        if (_uiState.value.isLoading || _uiState.value.isRegistered) {
            return
        }

        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = null,
                registerError = null
            )
        }
    }

    fun register() {
        val currentState = _uiState.value

        // Mencegah pengiriman registrasi berulang.
        if (currentState.isLoading || currentState.isRegistered) {
            return
        }

        val email = currentState.email.trim()
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        val emailError = AuthValidator.validateEmail(email)
        val passwordError =
            AuthValidator.validateRegisterPassword(password)
        val confirmPasswordError =
            AuthValidator.validateConfirmPassword(
                password = password,
                confirmPassword = confirmPassword
            )

        _uiState.update {
            it.copy(
                email = email,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                registerError = null
            )
        }

        // Hentikan proses jika ada input yang belum valid.
        if (
            emailError != null ||
            passwordError != null ||
            confirmPasswordError != null
        ) {
            return
        }

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val result = authRepository.registerCaregiver(
                    email = email,
                    password = password
                )

                val failure = result.exceptionOrNull()

                if (failure is CancellationException) {
                    throw failure
                }

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            password = "",
                            confirmPassword = "",
                            isRegistered = true,
                            registerError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            registerError =
                               result.exceptionOrNull()?.message
                                   ?:"Unable to register. Please try again."
                        )
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        registerError =
                            "Unable to register. Please try again."
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