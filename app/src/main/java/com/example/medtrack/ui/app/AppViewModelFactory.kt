package com.example.medtrack.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medtrack.repository.AuthRepository
import com.example.medtrack.repository.LastVisitedPageRepository

class AppViewModelFactory(
    private val authRepository: AuthRepository,
    private val lastVisitedPageRepository: LastVisitedPageRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(
                authRepository = authRepository,
                lastVisitedPageRepository = lastVisitedPageRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}