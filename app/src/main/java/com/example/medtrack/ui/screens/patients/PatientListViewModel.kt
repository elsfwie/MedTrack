package com.example.medtrack.ui.screens.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medtrack.repository.PatientRepository
import com.example.medtrack.repository.SharingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PatientListUiState(
    val loading: Boolean = true,
    val patients: List<PatientListItem> = emptyList(),
    val sharedPatients: List<PatientListItem> = emptyList(),
    val error: String? = null
)

class PatientListViewModel(
    private val repository: PatientRepository,
    private val sharingRepository: SharingRepository
) : ViewModel() {

    private val sharedPatients =
        MutableStateFlow<List<PatientListItem>>(emptyList())

    private val isLoadingShared = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val ownPatients = repository.observePatients()
        .map { patients ->
            patients.map { it.toPatientListItem() }
        }

    val uiState = combine(
        ownPatients,
        sharedPatients,
        isLoadingShared,
        errorMessage
    ) { own, shared, loading, error ->
        PatientListUiState(
            loading = loading,
            patients = own,
            sharedPatients = shared,
            error = error
        )
    }.catch { exception ->
        if (exception is CancellationException) {
            throw exception
        }

        emit(
            PatientListUiState(
                loading = false,
                error = "Gagal membaca data pasien."
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PatientListUiState()
    )

    init {
        refreshSharedPatients()
    }

    fun refreshSharedPatients() {
        viewModelScope.launch {
            isLoadingShared.value = true
            errorMessage.value = null

            try {
                sharedPatients.value = sharingRepository
                    .getAccessiblePatients()
                    .map { it.toPatientListItem() }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                errorMessage.value =
                    "Gagal memuat pasien yang dibagikan."
            } finally {
                isLoadingShared.value = false
            }
        }
    }

    fun deletePatients(patientIds: Set<String>) {
        viewModelScope.launch {
            patientIds.forEach { patientId ->
                repository.deletePatient(patientId)
            }
        }
    }

    class Factory(
        private val repository: PatientRepository,
        private val sharingRepository: SharingRepository
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
            return PatientListViewModel(
                repository = repository,
                sharingRepository = sharingRepository
            ) as T
        }
    }
}