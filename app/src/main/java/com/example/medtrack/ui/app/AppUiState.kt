package com.example.medtrack.ui.app

import com.example.medtrack.model.AuthUser
import com.example.medtrack.model.LastVisitedPage

sealed interface AppUiState {

    data object CheckingSession : AppUiState

    data object SignedOut : AppUiState

    data class SignedIn(
        val user: AuthUser,
        val initialPage: LastVisitedPage
    ) : AppUiState
}