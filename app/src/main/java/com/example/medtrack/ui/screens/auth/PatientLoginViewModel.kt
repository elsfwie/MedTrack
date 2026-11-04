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
    private val authRepository: AuthRepository,
    private val deviceId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientLoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onCodeChanged(code: String) {
        if (_uiState.value.isLoading || _uiState.value.isLoggedIn) {
            return
        }

        _uiState.update {
            it.copy(
                code = code.uppercase(),
                codeError = null,
                loginError = null
            )
        }
    }

    fun onAgeChanged(age: String) {
        if (_uiState.value.isLoading || _uiState.value.isLoggedIn) return
        if (age.length > 3 || age.any { it !in '0'..'9' }) return

        _uiState.update {
            it.copy(age = age, ageError = null, loginError = null)
        }
    }

    fun login() {
        val currentState = _uiState.value

        if (currentState.isLoading || currentState.isLoggedIn) {
            return
        }

        val code = currentState.code
        val codeError = AuthValidator.validatePatientCode(code)
        val parsedAge = currentState.age.toIntOrNull()
        val ageError = AuthValidator.validatePatientAge(currentState.age)

        _uiState.update {
            it.copy(
                codeError = codeError,
                ageError = ageError,
                loginError = null
            )
        }

        if (codeError != null || ageError != null || parsedAge == null) {
            return
        }

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val result = authRepository.loginPatient(
                    code = code,
                    age = parsedAge,
                    deviceId = deviceId
                )

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