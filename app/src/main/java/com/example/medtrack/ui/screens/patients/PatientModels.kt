package com.example.medtrack.ui.screens.patients

import com.example.medtrack.model.Patient
import com.example.medtrack.model.sharing.AccessiblePatient
// Data form patient
data class PatientFormData(
    val name: String,
    val gender: String,
    val age: Int,
    val emergencyContactName: String,
    val emergencyContact: String,
    val medicalNotes: String
)

// Data negara
data class CountryCode(
    val name: String,
    val flag: String,
    val code: String
)
//data list patient
data class PatientListItem(
    val id: String,
    val name: String,
    val age: Int,
    val gender: String = "Not specified",
    val emergencyContact: String?,
    val sharedBy: String? = null,
    val isOwned: Boolean
)

// Daftar negara
val countryCodes = listOf(
    CountryCode("Indonesia", "🇮🇩", "+62"),
    CountryCode("Singapore", "🇸🇬", "+65"),
    CountryCode("Malaysia", "🇲🇾", "+60"),
    CountryCode("United States", "🇺🇸", "+1"),
    CountryCode("United Kingdom", "🇬🇧", "+44"),
    CountryCode("Australia", "🇦🇺", "+61"),
    CountryCode("Japan", "🇯🇵", "+81"),
    CountryCode("South Korea", "🇰🇷", "+82"),
    CountryCode("India", "🇮🇳", "+91"),
    CountryCode("Philippines", "🇵🇭", "+63"),
    CountryCode("Thailand", "🇹🇭", "+66"),
    CountryCode("Vietnam", "🇻🇳", "+84")
)

fun Patient.toPatientListItem() = PatientListItem(
    id = id,
    name = name,
    age = age,
    gender = gender,
    emergencyContact = emergencyContact,
    isOwned = true
)

fun AccessiblePatient.toPatientListItem() = PatientListItem(
    id = id,
    name = name,
    age = age,
    emergencyContact = emergencyContact,
    sharedBy = sharedBy,
    isOwned = false
)