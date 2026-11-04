package com.example.medtrack.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medtrack.repository.AuthRepository

class PatientLoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val deviceId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                PatientLoginViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return PatientLoginViewModel(authRepository, deviceId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}