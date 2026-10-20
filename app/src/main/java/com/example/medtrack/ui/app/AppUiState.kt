package com.example.medtrack.ui.app

import com.example.medtrack.model.AuthUser

sealed interface AppUiState {

    data class CheckingSession(
        val errorMessage: String? = null
    ) : AppUiState

    data object SignedOut : AppUiState

    data class SignedIn(
        val user: AuthUser
    ) : AppUiState
}