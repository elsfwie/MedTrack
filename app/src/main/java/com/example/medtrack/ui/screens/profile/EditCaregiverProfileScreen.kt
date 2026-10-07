package com.example.medtrack.ui.screens.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medtrack.model.AuthUser
import com.example.medtrack.repository.LocalDataRepository
import com.example.medtrack.ui.components.BackButton
import com.example.medtrack.ui.components.Header

@Composable
fun EditCaregiverProfileScreen(
    user: AuthUser,
    repository: LocalDataRepository,
    onBackClick: () -> Unit
) {
    val model = caregiverProfileViewModel(user, repository)
    val state by model.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onBackClick()
        }
    }

    BackHandler(enabled = state.isSaving) {
        // Menunggu transaksi penyimpanan selesai.
    }

    Scaffold(
        topBar = { Header(title = "Edit Profile") },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BackButton(
                onBackClick = {
                    if (!state.isSaving) {
                        onBackClick()
                    }
                }
            )

            if (state.isLoading) {
                Text("Loading...")
            } else {
                val canEdit = state.profile != null &&
                        !state.isSaving && !state.isSaved

                OutlinedTextField(
                    value = state.name,
                    onValueChange = model::onNameChanged,
                    label = { Text("Name") },
                    enabled = canEdit,
                    singleLine = true,
                    isError = state.nameError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                state.nameError?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                OutlinedTextField(
                    value = state.email,
                    onValueChange = model::onEmailChanged,
                    label = { Text("Email") },
                    enabled = canEdit,
                    singleLine = true,
                    isError = state.emailError != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                state.emailError?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.error?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = model::save,
                    enabled = canEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (state.isSaving) "Saving..." else "Save")
                }
            }
        }
    }
}