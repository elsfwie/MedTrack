package com.example.medtrack.navigation

import com.example.medtrack.model.LastVisitedPage
import com.example.medtrack.model.UserRole

fun resolveAuthenticatedRoute(
    role: UserRole,
    page: LastVisitedPage
): String {
    return when (role) {
        UserRole.CAREGIVER -> {
            when (page) {
                LastVisitedPage.DASHBOARD ->
                    NavRoute.CaregiverHome.route

                LastVisitedPage.PROFILE ->
                    NavRoute.CaregiverProfile.route
            }
        }

        UserRole.PATIENT -> {
            when (page) {
                LastVisitedPage.DASHBOARD ->
                    NavRoute.PatientHome.route

                LastVisitedPage.PROFILE ->
                    NavRoute.PatientProfile.route
            }
        }
    }
}