package com.example.medtrack.model.schedule

data class MedicationScheduleDraft(
    val patientId: String,
    val medicineName: String,
    val dosage: String,
    val dosageForm: String,
    val selectedDays: Set<Int>,
    val isAllDay: Boolean,
    val reminderTimes: List<String>
)