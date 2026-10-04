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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import com.example.medtrack.repository.FakeAuthRepository
import com.example.medtrack.ui.screens.auth.LoginViewModel
import com.example.medtrack.ui.screens.auth.LoginViewModelFactory
import com.example.medtrack.ui.screens.auth.RegisterViewModel
import com.example.medtrack.ui.screens.auth.RegisterViewModelFactory
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.medtrack.ui.screens.auth.SessionViewModel
import com.example.medtrack.ui.screens.auth.SessionViewModelFactory
import com.example.medtrack.ui.screens.auth.PatientLoginViewModel
import com.example.medtrack.ui.screens.auth.PatientLoginViewModelFactory

@Composable
fun MedTrackNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

    val authRepository = remember {
        FakeAuthRepository()
    }

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
        composable(NavRoute.CaregiverLogin.route) { backStackEntry ->

            val loginViewModel = remember(
                backStackEntry,
                authRepository
            ) {
                val factory = LoginViewModelFactory(authRepository)

                ViewModelProvider(
                    backStackEntry,
                    factory
                )[LoginViewModel::class.java]
            }

            val uiState by loginViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isLoggedIn) {
                if (uiState.isLoggedIn) {
                    navController.navigate(
                        NavRoute.CaregiverHome.route
                    ) {
                        popUpTo(NavRoute.Splash.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

            CaregiverLoginScreen(
                uiState = uiState,
                onEmailChanged = loginViewModel::onEmailChanged,
                onPasswordChanged = loginViewModel::onPasswordChanged,
                onLoginClick = loginViewModel::login,

                onBackClick = {
                    navController.popBackStack()
                },

                onSignUpClick = {
                    navController.navigate(
                        NavRoute.CaregiverRegister.route
                    )
                }
            )
        }

        composable(NavRoute.CaregiverRegister.route) { backStackEntry ->

            val registerViewModel = remember(
                backStackEntry,
                authRepository
            ) {
                val factory = RegisterViewModelFactory(authRepository)

                ViewModelProvider(
                    backStackEntry,
                    factory
                )[RegisterViewModel::class.java]
            }

            val uiState by registerViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isRegistered) {
                if (uiState.isRegistered) {
                    navController.navigate(
                        NavRoute.CaregiverHome.route
                    ) {
                        popUpTo(NavRoute.Splash.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

            CaregiverRegisterScreen(
                uiState = uiState,

                onEmailChanged =
                    registerViewModel::onEmailChanged,

                onPasswordChanged =
                    registerViewModel::onPasswordChanged,

                onConfirmPasswordChanged =
                    registerViewModel::onConfirmPasswordChanged,

                onSignUpClick = registerViewModel::register,

                onBackClick = {
                    navController.popBackStack()
                },

                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoute.CaregiverProfile.route) { backStackEntry ->

            val sessionViewModel = remember(
                backStackEntry,
                authRepository
            ) {
                val factory = SessionViewModelFactory(authRepository)

                ViewModelProvider(
                    backStackEntry,
                    factory
                )[SessionViewModel::class.java]
            }

            val sessionState by sessionViewModel.uiState.collectAsState()

            LaunchedEffect(sessionState.isLoggedOut) {
                if (sessionState.isLoggedOut) {
                    navController.navigate(NavRoute.Splash.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }

            LaunchedEffect(
                sessionState.logoutError,
                sessionState.isLoggingOut
            ) {
                val error = sessionState.logoutError

                if (!sessionState.isLoggingOut && error != null) {
                    Toast.makeText(
                        context,
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            CaregiverProfileScreen(
                selectedIndex = 3,

                onItemSelected = { index ->
                    if (
                        !sessionState.isLoggingOut &&
                        !sessionState.isLoggedOut
                    ) {
                        when (index) {
                            0 -> {
                                navController.popBackStack(
                                    route = NavRoute.CaregiverHome.route,
                                    inclusive = false
                                )
                            }
                        }
                    }
                },

                onLogoutClick = sessionViewModel::logout
            )
        }
        // patient login
        composable(NavRoute.PatientLogin.route) { backStackEntry ->

            val patientLoginViewModel = remember(
                backStackEntry,
                authRepository
            ) {
                val factory = PatientLoginViewModelFactory(authRepository)

                ViewModelProvider(
                    backStackEntry,
                    factory
                )[PatientLoginViewModel::class.java]
            }

            val uiState by patientLoginViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isLoggedIn) {
                if (uiState.isLoggedIn) {
                    navController.navigate(NavRoute.PatientHome.route) {
                        popUpTo(NavRoute.Splash.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            }

            PatientLoginScreen(
                uiState = uiState,
                onCodeChanged = patientLoginViewModel::onCodeChanged,
                onLoginClick = patientLoginViewModel::login,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoute.PatientProfile.route) { backStackEntry ->

            val sessionViewModel = remember(
                backStackEntry,
                authRepository
            ) {
                val factory = SessionViewModelFactory(authRepository)

                ViewModelProvider(
                    backStackEntry,
                    factory
                )[SessionViewModel::class.java]
            }

            val sessionState by sessionViewModel.uiState.collectAsState()

            LaunchedEffect(sessionState.isLoggedOut) {
                if (sessionState.isLoggedOut) {
                    navController.navigate(NavRoute.Splash.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }

            LaunchedEffect(
                sessionState.logoutError,
                sessionState.isLoggingOut
            ) {
                val error = sessionState.logoutError

                if (!sessionState.isLoggingOut && error != null) {
                    Toast.makeText(
                        context,
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            PatientProfileScreen(
                selectedIndex = 2,

                onItemSelected = { index ->
                    if (
                        !sessionState.isLoggingOut &&
                        !sessionState.isLoggedOut
                    ) {
                        when (index) {
                            0 -> {
                                navController.popBackStack(
                                    route = NavRoute.PatientHome.route,
                                    inclusive = false
                                )
                            }
                        }
                    }
                },

                onLogoutClick = sessionViewModel::logout
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