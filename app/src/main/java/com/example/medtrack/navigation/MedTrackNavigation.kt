package com.example.medtrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medtrack.ui.screens.auth.SelectRoleScreen
import com.example.medtrack.ui.screens.splash.SplashScreen
import com.example.medtrack.ui.screens.auth.CaregiverLoginScreen
import com.example.medtrack.ui.screens.auth.CaregiverRegisterScreen
import com.example.medtrack.ui.screens.auth.PatientLoginScreen
import com.example.medtrack.model.UserRole
import com.example.medtrack.ui.screens.home.CaregiverHomeScreen
import com.example.medtrack.ui.screens.home.PatientHomeScreen

object Routes {
    const val SPLASH = "splash"
    const val SELECT_ROLE = "select_role"

    const val CAREGIVER_LOGIN = "caregiver_login"
    const val CAREGIVER_REGISTER = "caregiver_register"
    const val PATIENT_LOGIN = "patient_login"

    const val CAREGIVER_DASHBOARD = "caregiver_dashboard"
    const val PATIENT_DASHBOARD = "patient_dashboard"
}

@Composable
fun MedTrackNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onGetStartedClick = {
                    navController.navigate(Routes.SELECT_ROLE)
                }
            )
        }

        // Select Role Screen
        composable(Routes.SELECT_ROLE) {

            SelectRoleScreen(
                onConfirm = { selectedRole ->

                    when (selectedRole) {

                        UserRole.CAREGIVER -> {
                            navController.navigate(Routes.CAREGIVER_LOGIN)
                        }

                        UserRole.PATIENT -> {
                            navController.navigate(Routes.PATIENT_LOGIN)
                        }
                    }

                }
            )
        }

        // Caregiver Login
        composable(Routes.CAREGIVER_LOGIN) {

            CaregiverLoginScreen(
                onBackClick = {
                    navController.popBackStack()
                },

                onLoginSuccess = {
                    navController.navigate(Routes.CAREGIVER_DASHBOARD) {

                        popUpTo(Routes.CAREGIVER_LOGIN) {
                            inclusive = true
                        }
                    }
                },

                onSignUpClick = {
                    navController.navigate(Routes.CAREGIVER_REGISTER)
                }
            )
        }

        composable(Routes.CAREGIVER_REGISTER) {

            CaregiverRegisterScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onLoginClick = {
                    navController.popBackStack()
                },

                onSignUpClick = {
                    navController.navigate(Routes.CAREGIVER_DASHBOARD) {

                        popUpTo(Routes.CAREGIVER_LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // patient login
        composable(Routes.PATIENT_LOGIN) {

            PatientLoginScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onLoginSuccess = {

                    navController.navigate(Routes.PATIENT_DASHBOARD) {

                        popUpTo(Routes.PATIENT_LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Routes.CAREGIVER_DASHBOARD) {
            CaregiverHomeScreen()
        }

        composable(Routes.PATIENT_DASHBOARD) {
            PatientHomeScreen()
        }
    }
}