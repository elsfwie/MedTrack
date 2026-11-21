package com.example.medtrack.navigation

sealed class NavRoute(val route: String) {

    object Splash : NavRoute("splash")
    object RoleSelection : NavRoute("role_selection")

    object CaregiverLogin : NavRoute("caregiver_login")
    object CaregiverRegister : NavRoute("caregiver_register")
    object PatientLogin : NavRoute("patient_login")

    object PatientVerifyAge : NavRoute("patient_verify_age")

    object CaregiverHome : NavRoute("caregiver_home")
    object PatientHome : NavRoute("patient_home")

    object Patient : NavRoute("patient")

    object PatientDetail : NavRoute("patient_detail/{patientId}") {
        fun createRoute(patientId: String) = "patient_detail/$patientId"
    }

    object EditPatientProfile : NavRoute("edit_patient_profile/{patientId}") {
        fun createRoute(patientId: String) = "edit_patient_profile/$patientId"
    }

    object CaregiverShared : NavRoute("caregiver_shared")
    object PatientShared : NavRoute("patient_shared")

    object CaregiverProfile : NavRoute("caregiver_profile")
    object EditCaregiverProfile : NavRoute("edit_caregiver_profile")
    object PatientProfile : NavRoute("patient_profile")
    object AddPatient : NavRoute("add_patient")
}