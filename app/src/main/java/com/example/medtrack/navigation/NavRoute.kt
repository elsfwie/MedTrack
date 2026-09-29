package com.example.medtrack.navigation

sealed class NavRoute(val route: String) {

    object Splash : NavRoute("splash")
    object RoleSelection : NavRoute("role_selection")

    object CaregiverLogin : NavRoute("caregiver_login")
    object CaregiverRegister : NavRoute("caregiver_register")
    object PatientLogin : NavRoute("patient_login")

    object CaregiverHome : NavRoute("caregiver_home")
    object PatientHome : NavRoute("patient_home")

    object Patient : NavRoute("patient")

    object CaregiverShared : NavRoute("caregiver_shared")
    object PatientShared : NavRoute("patient_shared")

    object CaregiverProfile : NavRoute("caregiver_profile")
    object PatientProfile : NavRoute("patient_profile")
}