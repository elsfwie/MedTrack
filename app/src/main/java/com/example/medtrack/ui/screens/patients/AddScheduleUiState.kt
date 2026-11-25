package com.example.medtrack.ui.screens.patients

data class AddScheduleUiState(
    val medicineName: String = "",
    val dosage: String = "",
    val dosageForm: String = "",
    val selectedDays: Set<Int> = emptySet(),
    val isAllDay: Boolean = false,
    val frequency: Int = 1,
    val reminderTimes: List<String> = listOf("Set time"),
    val medicineNameError: String? = null,
    val dosageError: String? = null,
    val dosageFormError: String? = null,
    val daysError: String? = null,
    val timesError: String? = null,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)