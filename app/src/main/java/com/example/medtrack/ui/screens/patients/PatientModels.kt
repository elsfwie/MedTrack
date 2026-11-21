package com.example.medtrack.ui.screens.patients

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