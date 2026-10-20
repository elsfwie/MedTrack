package com.example.medtrack.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medtrack.model.AuthSession
import com.example.medtrack.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AppViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<AppUiState> =
        combine(
            authRepository.sessionState,
            authRepository.restoreError
        ) { session, restoreError ->
            when (session) {
                AuthSession.Checking -> {
                    AppUiState.CheckingSession(
                        errorMessage = restoreError
                    )
                }

                AuthSession.SignedOut -> {
                    AppUiState.SignedOut
                }

                is AuthSession.SignedIn -> {
                    AppUiState.SignedIn(
                        user = session.user
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppUiState.CheckingSession()
        )

    fun retryRestoreSession() {
        authRepository.restoreSession()
    }
}