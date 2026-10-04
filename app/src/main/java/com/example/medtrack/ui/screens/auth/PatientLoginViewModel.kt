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

class PatientLoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientLoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onCodeChanged(code: String) {
        if (_uiState.value.isLoading || _uiState.value.isLoggedIn) {
            return
        }

        _uiState.update {
            it.copy(
                code = code,
                codeError = null,
                loginError = null
            )
        }
    }

    fun login() {
        val currentState = _uiState.value

        if (currentState.isLoading || currentState.isLoggedIn) {
            return
        }

        val code = currentState.code
        val codeError = AuthValidator.validatePatientCode(code)

        _uiState.update {
            it.copy(
                codeError = codeError,
                loginError = null
            )
        }

        if (codeError != null) {
            return
        }

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val result = authRepository.loginPatient(code)

                val failure = result.exceptionOrNull()

                if (failure is CancellationException) {
                    throw failure
                }

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            code = "",
                            isLoggedIn = true,
                            loginError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            loginError =
                                "Unable to log in. Please check your code."
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