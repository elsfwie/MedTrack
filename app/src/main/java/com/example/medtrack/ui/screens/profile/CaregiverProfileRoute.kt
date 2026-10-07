package com.example.medtrack.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medtrack.model.AuthUser
import com.example.medtrack.repository.LocalDataRepository

@Composable
internal fun caregiverProfileViewModel(
    user: AuthUser,
    repository: LocalDataRepository
): CaregiverProfileViewModel {
    val factory = remember(user.id, repository) {
        viewModelFactory {
            initializer {
                CaregiverProfileViewModel(user, repository)
            }
        }
    }

    return viewModel(factory = factory)
}

@Composable
fun CaregiverProfileRoute(
    user: AuthUser,
    repository: LocalDataRepository,
    onItemSelected: (Int) -> Unit,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    canInteract: Boolean
) {
    val model = caregiverProfileViewModel(user, repository)
    val state by model.uiState.collectAsStateWithLifecycle()

    CaregiverProfileScreen(
        selectedIndex = 3,
        onItemSelected = onItemSelected,
        caregiverName = state.profile?.name
            ?: if (state.isLoading) "Loading..." else "Profile unavailable",
        email = state.profile?.email.orEmpty(),
        patientCount = 0,
        caregiverCount = 0,
        onLogoutClick = onLogoutClick,
        onEditProfileClick = onEditProfileClick,
        canEdit = canInteract && state.profile != null && !state.isLoading,
        profileMessage = state.error
    )
}