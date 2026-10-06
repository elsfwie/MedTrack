package com.example.medtrack.model

sealed interface AuthSession {

    data object Checking : AuthSession

    data object SignedOut : AuthSession

    data class SignedIn(
        val user: AuthUser
    ) : AuthSession
}