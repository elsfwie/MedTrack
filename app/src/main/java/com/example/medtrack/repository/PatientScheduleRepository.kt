package com.example.medtrack.repository

import com.example.medtrack.model.schedule.MedicationScheduleDraft
interface PatientScheduleRepository {
    suspend fun addSchedule(
        patientId: String,
        medicineName: String,
        dosage: String,
        dosageForm: String,
        selectedDays: Set<Int>,
        frequency: Int,
        reminderTimes: List<String>
    )
}