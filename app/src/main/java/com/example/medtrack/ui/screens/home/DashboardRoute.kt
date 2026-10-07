package com.example.medtrack.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.UserRole
import com.example.medtrack.repository.LocalDataRepository

@Composable
fun DashboardRoute(
    user: AuthUser,
    repository: LocalDataRepository,
    onItemSelected: (Int) -> Unit
) {
    val factory = remember(user.id, repository) {
        viewModelFactory {
            initializer {
                DashboardViewModel(
                    user = user,
                    repository = repository,
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }

    val model: DashboardViewModel = viewModel(factory = factory)
    val state by model.uiState.collectAsStateWithLifecycle()

    if (user.role == UserRole.CAREGIVER) {
        CaregiverHomeScreen(
            uiState = state,
            onDateSelected = model::selectDate,
            onItemSelected = onItemSelected
        )
    } else {
        PatientHomeScreen(
            uiState = state,
            onDateSelected = model::selectDate,
            onItemSelected = onItemSelected
        )
    }
}

@Composable
internal fun DashboardContent(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            uiState.isLoading -> {
                item { Text("Loading...") }
            }

            uiState.error != null -> {
                item { Text(uiState.error) }
            }

            uiState.medications.isEmpty() -> {
                item { Text("No medications for this date") }
            }

            else -> {
                item {
                    Text(
                        text = "Medications: ${uiState.medications.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(
                    items = uiState.medications,
                    key = { it.id }
                ) { medication ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "${medication.time}  ${medication.medicineName}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${medication.dose} · ${medication.status}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}