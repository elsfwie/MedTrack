package com.example.medtrack.model

data class Patient(
    val id: String,
    val createdBy: String,
    val name: String,
    val age: Int,
    val emergencyContact: String?,
    val gender: String = "Not specified",
    val medicalNotes: String = ""
)