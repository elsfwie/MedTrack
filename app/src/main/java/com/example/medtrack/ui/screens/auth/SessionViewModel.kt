package com.example.medtrack.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medtrack.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionUiState(
    val isLoggingOut: Boolean = false,
    val logoutError: String? = null,
    val isLoggedOut: Boolean = false
)

class SessionViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState = _uiState.asStateFlow()

    fun logout() {
        if (_uiState.value.isLoggingOut || _uiState.value.isLoggedOut) {
            return
        }

        _uiState.update {
            it.copy(
                isLoggingOut = true,
                logoutError = null
            )
        }

        viewModelScope.launch {
            try {
                val result = authRepository.logout()

                val failure = result.exceptionOrNull()
                if (failure is CancellationException) {
                    throw failure
                }

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(isLoggedOut = true)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            logoutError =
                                "Unable to log out. Please try again."
                        )
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        logoutError =
                            "Unable to log out. Please try again."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isLoggingOut = false)
                }
            }
        }
    }
}