package com.example.medtrack.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medtrack.repository.AuthRepository

class PatientLoginViewModelFactory(
    private val authRepository: AuthRepository
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
            return PatientLoginViewModel(authRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}