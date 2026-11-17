package com.example.medtrack.model.demo

import com.example.medtrack.model.Patient

object PatientDemoData {

    fun forCaregiver(caregiverId: String): List<Patient> {
        return listOf(
            Patient(
                id = "sample-$caregiverId-patient-01",
                createdBy = caregiverId,
                name = "Budi Santoso",
                age = 68,
                emergencyContact = null,
                gender = "Male",
                medicalNotes = "Data dummy untuk pengujian."
            ),
            Patient(
                id = "sample-$caregiverId-patient-02",
                createdBy = caregiverId,
                name = "Siti Rahma",
                age = 72,
                emergencyContact = null,
                gender = "Female",
                medicalNotes = "Data dummy untuk pengujian."
            )
        )
    }
}