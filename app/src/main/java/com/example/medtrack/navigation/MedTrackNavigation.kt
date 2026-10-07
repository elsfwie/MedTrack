package com.example.medtrack.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medtrack.MedTrackApplication
import com.example.medtrack.model.AuthSession
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.LastVisitedPage
import com.example.medtrack.model.UserRole
import com.example.medtrack.repository.FakeAuthRepository
import com.example.medtrack.ui.screens.auth.CaregiverLoginScreen
import com.example.medtrack.ui.screens.auth.CaregiverRegisterScreen
import com.example.medtrack.ui.screens.auth.LoginViewModel
import com.example.medtrack.ui.screens.auth.LoginViewModelFactory
import com.example.medtrack.ui.screens.auth.PatientLoginScreen
import com.example.medtrack.ui.screens.auth.PatientLoginViewModel
import com.example.medtrack.ui.screens.auth.PatientLoginViewModelFactory
import com.example.medtrack.ui.screens.auth.RegisterViewModel
import com.example.medtrack.ui.screens.auth.RegisterViewModelFactory
import com.example.medtrack.ui.screens.auth.SelectRoleScreen
import com.example.medtrack.ui.screens.auth.SessionViewModel
import com.example.medtrack.ui.screens.auth.SessionViewModelFactory
import com.example.medtrack.ui.screens.home.CaregiverHomeScreen
import com.example.medtrack.ui.screens.home.PatientHomeScreen
import com.example.medtrack.ui.screens.profile.CaregiverProfileScreen
import com.example.medtrack.ui.screens.profile.PatientProfileScreen
import com.example.medtrack.ui.screens.splash.SplashScreen

@Composable
fun MedTrackNavigation() {
    val application =
        LocalContext.current.applicationContext as MedTrackApplication
    val authRepository = application.authRepository

    val session by authRepository.sessionState.collectAsStateWithLifecycle()
    val restoreError by authRepository.restoreError.collectAsStateWithLifecycle()

    if (session == AuthSession.Checking) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(restoreError ?: "Loading...")

            if (restoreError != null) {
                TextButton(onClick = authRepository::restoreSession) {
                    Text("Retry")
                }
            }
        }
        return
    }

    val user = (session as? AuthSession.SignedIn)?.user

    key(user?.id, user?.role) {
        MedTrackGraph(
            application = application,
            authRepository = authRepository,
            user = user
        )
    }
}

@Composable
private fun MedTrackGraph(
    application: MedTrackApplication,
    authRepository: FakeAuthRepository,
    user: AuthUser?
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val caregiverRoutes = listOf(
        NavRoute.CaregiverHome.route,
        null,
        null,
        NavRoute.CaregiverProfile.route
    )

    val patientRoutes = listOf(
        NavRoute.PatientHome.route,
        null,
        NavRoute.PatientProfile.route
    )

    val onCaregiverTab: (Int) -> Unit = { index ->
        caregiverRoutes.getOrNull(index)?.let { route ->
            navController.openTab(
                route,
                NavRoute.CaregiverHome.route
            )
        }
    }

    val onPatientTab: (Int) -> Unit = { index ->
        patientRoutes.getOrNull(index)?.let { route ->
            navController.openTab(
                route,
                NavRoute.PatientHome.route
            )
        }
    }

    val startRoute = if (user == null) {
        NavRoute.Splash.route
    } else {
        resolveAuthenticatedRoute(
            user.role,
            LastVisitedPage.DASHBOARD
        )
    }

    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
        if (user == null) {
            composable(NavRoute.Splash.route) {
                SplashScreen(
                    onGetStartedClick = {
                        navController.navigate(
                            NavRoute.RoleSelection.route
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoute.RoleSelection.route) {
                SelectRoleScreen(
                    onConfirm = { role ->
                        val route =
                            if (role == UserRole.CAREGIVER) {
                                NavRoute.CaregiverLogin.route
                            } else {
                                NavRoute.PatientLogin.route
                            }

                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoute.CaregiverLogin.route) {
                val model: LoginViewModel = viewModel(
                    factory = remember(authRepository) {
                        LoginViewModelFactory(authRepository)
                    }
                )

                val state by model.uiState.collectAsStateWithLifecycle()

                CaregiverLoginScreen(
                    uiState = state,
                    onEmailChanged = model::onEmailChanged,
                    onPasswordChanged = model::onPasswordChanged,
                    onLoginClick = model::login,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSignUpClick = {
                        navController.navigate(
                            NavRoute.CaregiverRegister.route
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoute.CaregiverRegister.route) {
                val model: RegisterViewModel = viewModel(
                    factory = remember(authRepository) {
                        RegisterViewModelFactory(authRepository)
                    }
                )

                val state by model.uiState.collectAsStateWithLifecycle()

                CaregiverRegisterScreen(
                    uiState = state,
                    onEmailChanged = model::onEmailChanged,
                    onPasswordChanged = model::onPasswordChanged,
                    onConfirmPasswordChanged =
                        model::onConfirmPasswordChanged,
                    onSignUpClick = model::register,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(NavRoute.PatientLogin.route) {
                val model: PatientLoginViewModel = viewModel(
                    factory = remember(authRepository) {
                        PatientLoginViewModelFactory(authRepository)
                    }
                )

                val state by model.uiState.collectAsStateWithLifecycle()

                PatientLoginScreen(
                    uiState = state,
                    onCodeChanged = model::onCodeChanged,
                    onLoginClick = model::login,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        } else if (user.role == UserRole.CAREGIVER) {
            composable(NavRoute.CaregiverHome.route) {
                CaregiverHomeScreen(
                    onItemSelected = onCaregiverTab
                )
            }

            composable(NavRoute.CaregiverProfile.route) {
                val model: SessionViewModel = viewModel(
                    factory = remember(authRepository) {
                        SessionViewModelFactory(authRepository)
                    }
                )

                val state by model.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(state.logoutError) {
                    state.logoutError?.let { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                CaregiverProfileScreen(
                    selectedIndex = 3,
                    onItemSelected = { index ->
                        if (
                            !state.isLoggingOut &&
                            !state.isLoggedOut
                        ) {
                            onCaregiverTab(index)
                        }
                    },
                    onLogoutClick = model::logout
                )
            }
        } else {
            composable(NavRoute.PatientHome.route) {
                PatientHomeScreen(
                    onItemSelected = onPatientTab
                )
            }

            composable(NavRoute.PatientProfile.route) {
                val model: SessionViewModel = viewModel(
                    factory = remember(authRepository) {
                        SessionViewModelFactory(authRepository)
                    }
                )

                val state by model.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(state.logoutError) {
                    state.logoutError?.let { message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                PatientProfileScreen(
                    selectedIndex = 2,
                    onItemSelected = { index ->
                        if (
                            !state.isLoggingOut &&
                            !state.isLoggedOut
                        ) {
                            onPatientTab(index)
                        }
                    },
                    onLogoutClick = model::logout
                )
            }
        }
    }
}

private fun NavHostController.openTab(
    route: String,
    homeRoute: String
) {
    if (currentDestination?.route == route) {
        return
    }

    navigate(route) {
        popUpTo(homeRoute) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}