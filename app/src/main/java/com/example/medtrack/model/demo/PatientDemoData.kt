package com.example.medtrack.model.demo

import com.example.medtrack.model.Patient

object PatientDemoData {
    fun forCaregiver(caregiverId: String): List<Patient> = listOf(
        Patient(
            id = "demo-patient-001",
            createdBy = caregiverId,
            name = "Budi Santoso",
            age = 68,
            emergencyContact = null,
            gender = "Male",
            medicalNotes = "Data contoh untuk pengujian."
        ),
        Patient(
            id = "demo-patient-002",
            createdBy = caregiverId,
            name = "Siti Rahma",
            age = 72,
            emergencyContact = null,
            gender = "Female",
            medicalNotes = "Data contoh untuk pengujian."
        )
    )
}