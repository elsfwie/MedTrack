package com.example.medtrack.ui.screens.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medtrack.repository.PatientRepository
import com.example.medtrack.validation.AuthValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPatientViewModel(
    private val repository: PatientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPatientUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.update {
            it.copy(
                name = value,
                nameError = null,
                errorMessage = null
            )
        }
    }

    fun onGenderChanged(value: String) {
        _uiState.update {
            it.copy(
                gender = value,
                genderError = null,
                errorMessage = null
            )
        }
    }

    fun onAgeChanged(value: String) {
        if (value.length <= 3 && value.all(Char::isDigit)) {
            _uiState.update {
                it.copy(
                    age = value,
                    ageError = null,
                    errorMessage = null
                )
            }
        }
    }

    fun onCountryCodeChanged(value: String) {
        _uiState.update {
            it.copy(
                countryCode = value,
                phoneError = null
            )
        }
    }

    fun onPhoneChanged(value: String) {
        val maxDigits = AuthValidator.maxLocalPhoneDigits(
            _uiState.value.countryCode
        )
        val digits = value.filter(Char::isDigit)
        val containsOnlyPhoneCharacters = value.all { char ->
            char.isDigit() || char == ' ' || char == '-'
        }

        if (containsOnlyPhoneCharacters && digits.length <= maxDigits) {
            _uiState.update {
                it.copy(
                    phoneNumber = digits,
                    phoneError = null
                )
            }
        }
    }

    fun onMedicalNotesChanged(value: String) {
        _uiState.update {
            it.copy(medicalNotes = value)
        }
    }

    fun submit() {
        val current = _uiState.value
        if (current.isSaving || current.isSaved) return

        val cleanName = current.name.trim()
        val parsedAge = current.age.toIntOrNull()

        val nameError =
            if (cleanName.isBlank()) "Nama pasien wajib diisi." else null

        val genderError =
            if (current.gender !in setOf("Male", "Female")) {
                "Pilih gender pasien."
            } else {
                null
            }

        val ageError = when {
            current.age.isBlank() -> "Umur wajib diisi."
            parsedAge == null -> "Umur harus berupa angka."
            parsedAge !in 0..150 -> "Umur harus antara 0 sampai 150."
            else -> null
        }

        val phoneError = AuthValidator.validateEmergencyContact(
            localPhone = current.phoneNumber,
            countryCode = current.countryCode
        )

        _uiState.update {
            it.copy(
                name = cleanName,
                nameError = nameError,
                genderError = genderError,
                ageError = ageError,
                phoneError = phoneError,
                errorMessage = null
            )
        }

        if (
            nameError != null ||
            genderError != null ||
            ageError != null ||
            phoneError != null ||
            parsedAge == null
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                repository.addPatient(
                    name = cleanName,
                    age = parsedAge,
                    emergencyContact =
                        "${current.countryCode} ${current.phoneNumber}",
                    gender = current.gender,
                    medicalNotes = current.medicalNotes.trim()
                )

                _uiState.update { it.copy(isSaved = true) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Pasien gagal disimpan. Coba lagi."
                    )
                }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}