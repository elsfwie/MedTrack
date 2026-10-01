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

@Composable
fun MedTrackNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.Splash.route
    ) {

        // Splash Screen
        composable(NavRoute.Splash.route) {
            SplashScreen(
                onGetStartedClick = {
                    navController.navigate(NavRoute.RoleSelection.route)
                }
            )
        }

        // Select Role Screen
        composable(NavRoute.RoleSelection.route) {

            SelectRoleScreen(
                onConfirm = { selectedRole ->

                    when (selectedRole) {

                        UserRole.CAREGIVER -> {
                            navController.navigate(NavRoute.CaregiverLogin.route)
                        }

                        UserRole.PATIENT -> {
                            navController.navigate(NavRoute.PatientLogin.route)
                        }
                    }

                }
            )
        }

        // Caregiver Login
        composable(NavRoute.CaregiverLogin.route) {

            CaregiverLoginScreen(
                onBackClick = {
                    navController.popBackStack()
                },

                onLoginSuccess = {
                    navController.navigate(NavRoute.CaregiverHome.route) {

                        popUpTo(NavRoute.CaregiverLogin.route) {
                            inclusive = true
                        }
                    }
                },

                onSignUpClick = {
                    navController.navigate(NavRoute.CaregiverRegister.route)
                }
            )
        }

        composable(NavRoute.CaregiverRegister.route) {

            CaregiverRegisterScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onLoginClick = {
                    navController.popBackStack()
                },

                onSignUpClick = {
                    navController.navigate(NavRoute.CaregiverHome.route) {

                        popUpTo(NavRoute.CaregiverLogin.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // patient login
        composable(NavRoute.PatientLogin.route) {

            PatientLoginScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onLoginSuccess = {

                    navController.navigate(NavRoute.PatientHome.route) {

                        popUpTo(NavRoute.PatientLogin.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(NavRoute.CaregiverHome.route) {
            CaregiverHomeScreen()
        }

        composable(NavRoute.PatientHome.route) {
            PatientHomeScreen()
        }
    }
}