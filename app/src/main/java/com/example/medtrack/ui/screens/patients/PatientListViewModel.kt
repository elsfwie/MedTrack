package com.example.medtrack.ui.screens.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medtrack.model.Patient
import com.example.medtrack.repository.PatientRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class PatientListUiState(
    val loading: Boolean = true,
    val patients: List<Patient> = emptyList(),
    val error: String? = null
)

class PatientListViewModel(
    repository: PatientRepository
) : ViewModel() {

    val uiState = repository.observePatients()
        .map { patients ->
            PatientListUiState(
                loading = false,
                patients = patients
            )
        }
        .catch { exception ->
            if (exception is CancellationException) {
                throw exception
            }

            emit(
                PatientListUiState(
                    loading = false,
                    error = "Gagal membaca data pasien."
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PatientListUiState()
        )

    class Factory(
        private val repository: PatientRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            require(
                modelClass.isAssignableFrom(
                    PatientListViewModel::class.java
                )
            )

            @Suppress("UNCHECKED_CAST")
            return PatientListViewModel(repository) as T
        }
    }
}