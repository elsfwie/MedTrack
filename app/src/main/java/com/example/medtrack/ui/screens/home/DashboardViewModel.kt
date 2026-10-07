package com.example.medtrack.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.MedicationSchedule
import com.example.medtrack.repository.LocalDataRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class DashboardUiState(
    val date: LocalDate = LocalDate.now(),
    val isLoading: Boolean = true,
    val medications: List<MedicationSchedule> = emptyList(),
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val user: AuthUser,
    private val repository: LocalDataRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val selectedEpochDay = savedStateHandle.getStateFlow(
        "selected_epoch_day",
        LocalDate.now().toEpochDay()
    )

    val uiState: StateFlow<DashboardUiState> =
        selectedEpochDay.flatMapLatest { epochDay ->
            val date = LocalDate.ofEpochDay(epochDay)

            repository.observeSchedules(user, date)
                .map { medications ->
                    DashboardUiState(
                        date = date,
                        isLoading = false,
                        medications = medications
                    )
                }
                .onStart {
                    emit(DashboardUiState(date = date))
                }
                .catch { exception ->
                    if (exception is CancellationException) {
                        throw exception
                    }

                    emit(
                        DashboardUiState(
                            date = date,
                            isLoading = false,
                            error = "Unable to load data"
                        )
                    )
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState(
                date = LocalDate.ofEpochDay(selectedEpochDay.value)
            )
        )

    fun selectDate(date: LocalDate) {
        savedStateHandle["selected_epoch_day"] = date.toEpochDay()
    }

    fun addSchedule(
        medicineName: String,
        dose: String,
        time: String
    ) {
        if (!isScheduleFormValid(medicineName, dose, time)) return

        viewModelScope.launch {
            repository.addSchedule(
                MedicationSchedule(
                    ownerId = user.id,
                    id = UUID.randomUUID().toString(),
                    date = uiState.value.date.toString(),
                    medicineName = medicineName.trim(),
                    dose = dose.trim(),
                    time = time.trim(),
                    status = "Pending"
                )
            )
        }
    }

    fun updateSchedule(
        schedule: MedicationSchedule,
        medicineName: String,
        dose: String,
        time: String
    ) {
        if (!isScheduleFormValid(medicineName, dose, time)) return

        viewModelScope.launch {
            repository.updateSchedule(
                schedule.copy(
                    medicineName = medicineName.trim(),
                    dose = dose.trim(),
                    time = time.trim()
                )
            )
        }
    }

    fun deleteSchedule(schedule: MedicationSchedule) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }

    private fun isScheduleFormValid(
        medicineName: String,
        dose: String,
        time: String
    ): Boolean =
        medicineName.isNotBlank() &&
                dose.isNotBlank() &&
                time.isNotBlank()
}