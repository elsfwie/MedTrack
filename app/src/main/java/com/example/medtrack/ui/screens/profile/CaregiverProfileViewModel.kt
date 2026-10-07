package com.example.medtrack.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.UserProfile
import com.example.medtrack.repository.LocalDataRepository
import com.example.medtrack.validation.AuthValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CaregiverProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val name: String = "",
    val email: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

class CaregiverProfileViewModel(
    private val user: AuthUser,
    private val repository: LocalDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaregiverProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.observeProfile(user).collect { profile ->
                    _uiState.update { current ->
                        if (profile == null) {
                            current.copy(
                                profile = null,
                                isLoading = false,
                                error = "Profile was not found."
                            )
                        } else {
                            val initializeDraft = current.profile == null
                            current.copy(
                                profile = profile,
                                isLoading = false,
                                name = if (initializeDraft) {
                                    profile.name
                                } else {
                                    current.name
                                },
                                email = if (initializeDraft) {
                                    profile.email
                                } else {
                                    current.email
                                },
                                error = null
                            )
                        }
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Unable to load profile."
                    )
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        if (_uiState.value.isSaving || _uiState.value.isSaved) return

        _uiState.update {
            it.copy(name = name, nameError = null, error = null)
        }
    }

    fun onEmailChanged(email: String) {
        if (_uiState.value.isSaving || _uiState.value.isSaved) return

        _uiState.update {
            it.copy(email = email, emailError = null, error = null)
        }
    }

    fun save() {
        val current = _uiState.value

        if (
            current.isLoading ||
            current.isSaving ||
            current.isSaved ||
            current.profile == null
        ) {
            return
        }

        val name = current.name.trim()
        val email = current.email.trim()
        val nameError = if (name.isBlank()) "Name is required." else null
        val emailError = AuthValidator.validateEmail(email)

        _uiState.update {
            it.copy(
                name = name,
                email = email,
                nameError = nameError,
                emailError = emailError,
                error = null
            )
        }

        if (nameError != null || emailError != null) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                repository.updateCaregiverProfile(
                    userId = user.id,
                    name = name,
                    email = email
                )
                _uiState.update { it.copy(isSaved = true) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(error = "Unable to save profile. Please try again.")
                }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}