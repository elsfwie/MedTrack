package com.example.medtrack.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medtrack.model.AuthSession
import com.example.medtrack.model.LastVisitedPage
import com.example.medtrack.repository.AuthRepository
import com.example.medtrack.repository.LastVisitedPageRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppViewModel(
    private val authRepository: AuthRepository,
    private val lastVisitedPageRepository: LastVisitedPageRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<AppUiState>(AppUiState.CheckingSession)

    val uiState = _uiState.asStateFlow()

    init {
        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            authRepository.sessionState.collectLatest { session ->
                when (session) {
                    AuthSession.Checking -> {
                        _uiState.value = AppUiState.CheckingSession
                    }

                    AuthSession.SignedOut -> {
                        _uiState.value = AppUiState.SignedOut
                    }

                    is AuthSession.SignedIn -> {
                        _uiState.value = AppUiState.CheckingSession

                        val lastPage = readLastPage(session.user.id)

                        _uiState.value = AppUiState.SignedIn(
                            user = session.user,
                            initialPage = lastPage
                        )
                    }
                }
            }
        }
    }

    private suspend fun readLastPage(
        userId: String
    ): LastVisitedPage {
        return try {
            lastVisitedPageRepository.getLastPage(userId)
                ?: LastVisitedPage.DASHBOARD
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            // Gagal membaca riwayat tidak membatalkan sesi login.
            LastVisitedPage.DASHBOARD
        }
    }
}