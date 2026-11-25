package com.example.medtrack.ui.screens.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medtrack.repository.PatientRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PatientDetailViewModel(
    private val patientId: String,
    private val repository: PatientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val patient = repository.getPatient(patientId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        patient = patient,
                        errorMessage = if (patient == null) {
                            "Pasien tidak ditemukan."
                        } else {
                            null
                        }
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat data pasien."
                    )
                }
            }
        }
    }
}