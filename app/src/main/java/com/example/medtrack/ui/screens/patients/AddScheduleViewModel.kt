package com.example.medtrack.ui.screens.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medtrack.repository.PatientScheduleRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddScheduleViewModel(
    private val patientId: String,
    private val repository: PatientScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddScheduleUiState())
    val uiState = _uiState.asStateFlow()

    fun onMedicineNameChanged(value: String) {
        _uiState.update {
            it.copy(
                medicineName = value,
                medicineNameError = null,
                errorMessage = null
            )
        }
    }

    fun onDosageChanged(value: String) {
        _uiState.update {
            it.copy(
                dosage = value,
                dosageError = null,
                errorMessage = null
            )
        }
    }

    fun onDosageFormChanged(value: String) {
        _uiState.update {
            it.copy(
                dosageForm = value,
                dosageFormError = null,
                errorMessage = null
            )
        }
    }

    fun onDayToggled(dayIndex: Int) {
        if (dayIndex !in 0..6) return

        _uiState.update { current ->
            val newDays = if (dayIndex in current.selectedDays) {
                current.selectedDays - dayIndex
            } else {
                current.selectedDays + dayIndex
            }

            current.copy(
                selectedDays = newDays,
                isAllDay = newDays.size == 7,
                daysError = null,
                errorMessage = null
            )
        }
    }

    fun onAllDayChanged(isAllDay: Boolean) {
        _uiState.update {
            it.copy(
                isAllDay = isAllDay,
                selectedDays = if (isAllDay) (0..6).toSet() else emptySet(),
                daysError = null,
                errorMessage = null
            )
        }
    }

    // Nilai 0 hanya dipakai sementara ketika pengguna mengosongkan input.
    fun onFrequencyChanged(value: Int) {
        if (value !in 0..12) return

        _uiState.update { current ->
            val updatedTimes = if (value == 0) {
                current.reminderTimes
            } else {
                List(value) { index ->
                    current.reminderTimes.getOrNull(index) ?: "Set time"
                }
            }

            current.copy(
                frequency = value,
                reminderTimes = updatedTimes,
                timesError = null,
                errorMessage = null
            )
        }
    }

    fun onReminderTimeChanged(index: Int, time: String) {
        _uiState.update { current ->
            val visibleFrequency = current.frequency.coerceAtLeast(1)
            if (index !in 0 until visibleFrequency) return@update current

            val updatedTimes = current.reminderTimes
                .toMutableList()
                .apply {
                    while (size < visibleFrequency) {
                        add("Set time")
                    }
                    this[index] = time
                }

            current.copy(
                reminderTimes = updatedTimes,
                timesError = null,
                errorMessage = null
            )
        }
    }

    fun save() {
        val current = _uiState.value
        if (current.isSaving || current.isSaved) return

        val medicineName = current.medicineName.trim()
        val dosage = current.dosage.trim()

        val medicineNameError = if (medicineName.isBlank()) {
            "Nama obat wajib diisi."
        } else {
            null
        }

        val dosageError = if (dosage.isBlank()) {
            "Dosis obat wajib diisi."
        } else {
            null
        }

        val dosageFormError = if (current.dosageForm.isBlank()) {
            "Pilih bentuk obat."
        } else {
            null
        }

        val daysError = if (current.selectedDays.isEmpty()) {
            "Pilih minimal satu hari."
        } else {
            null
        }

        val timesError = when {
            current.frequency !in 1..12 ->
                "Frekuensi harus antara 1 sampai 12 kali sehari."

            current.reminderTimes
                .take(current.frequency)
                .any { it.isBlank() || it == "Set time" } ->
                "Tentukan semua waktu pengingat."

            else -> null
        }

        _uiState.update {
            it.copy(
                medicineName = medicineName,
                dosage = dosage,
                medicineNameError = medicineNameError,
                dosageError = dosageError,
                dosageFormError = dosageFormError,
                daysError = daysError,
                timesError = timesError,
                errorMessage = null
            )
        }

        if (
            medicineNameError != null ||
            dosageError != null ||
            dosageFormError != null ||
            daysError != null ||
            timesError != null
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true, errorMessage = null)
            }

            try {
                repository.addSchedule(
                    patientId = patientId,
                    medicineName = medicineName,
                    dosage = dosage,
                    dosageForm = current.dosageForm,
                    selectedDays = current.selectedDays,
                    frequency = current.frequency,
                    reminderTimes = current.reminderTimes.take(current.frequency)
                )

                _uiState.update {
                    it.copy(isSaved = true)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Jadwal obat gagal disimpan. Coba lagi."
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isSaving = false)
                }
            }
        }
    }

    class Factory(
        private val patientId: String,
        private val repository: PatientScheduleRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            require(
                modelClass.isAssignableFrom(AddScheduleViewModel::class.java)
            )

            @Suppress("UNCHECKED_CAST")
            return AddScheduleViewModel(
                patientId = patientId,
                repository = repository
            ) as T
        }
    }
}