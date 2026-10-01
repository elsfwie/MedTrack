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
import com.example.medtrack.ui.screens.profile.CaregiverProfileScreen
import com.example.medtrack.ui.screens.profile.PatientProfileScreen

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

        composable(NavRoute.CaregiverProfile.route) {

            CaregiverProfileScreen(
                selectedIndex = 3,
                onItemSelected = { index ->
                    when (index) {
                        0 -> {
                            navController.popBackStack(
                                route = NavRoute.CaregiverHome.route,
                                inclusive = false
                            )
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

        composable(NavRoute.PatientProfile.route) {
            PatientProfileScreen(
                selectedIndex = 2,
                onItemSelected = { index ->
                    when (index) {
                        0 -> {
                            navController.popBackStack(
                                route = NavRoute.PatientHome.route,
                                inclusive = false
                            )
                        }
                    }
                }
            )
        }

        composable(NavRoute.CaregiverHome.route) {
            CaregiverHomeScreen(
                onItemSelected = { index ->
                    when (index) {
                        3 -> {
                            navController.navigate(NavRoute.CaregiverProfile.route){
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }

        composable(NavRoute.PatientHome.route) {
            PatientHomeScreen(
                onItemSelected = { index ->
                    when (index) {
                        2 -> {
                            navController.navigate(NavRoute.PatientProfile.route){
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }
    }
}