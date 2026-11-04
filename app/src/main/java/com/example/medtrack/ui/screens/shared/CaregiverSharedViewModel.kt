package com.example.medtrack.ui.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medtrack.model.sharing.ConnectionLists
import com.example.medtrack.repository.SharingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CaregiverSharedViewModel(
    private val repository: SharingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SharingUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        refresh()
    }

    fun onTabSelected(tab: SharingTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(searchQuery = query, errorMessage = null)
        }

        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update {
                it.copy(searchResults = emptyList(), isSearching = false)
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)

            _uiState.update { it.copy(isSearching = true) }

            try {
                val results = repository.searchCaregivers(query.trim())
                _uiState.update {
                    it.copy(searchResults = results)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Unable to search caregivers.")
                }
            } finally {
                _uiState.update { it.copy(isSearching = false) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                val (connections, patients) = loadData()

                _uiState.update {
                    it.copy(
                        connections = connections,
                        patients = patients,
                        isLoading = false
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unable to load sharing data."
                    )
                }
            }
        }
    }

    fun sendConnectionRequest(caregiverId: String) {
        performAction {
            repository.sendConnectionRequest(caregiverId)
        }
    }

    fun acceptConnectionRequest(connectionId: String) {
        performAction {
            repository.acceptConnectionRequest(connectionId)
        }
    }

    fun declineConnectionRequest(connectionId: String) {
        performAction {
            repository.declineConnectionRequest(connectionId)
        }
    }

    fun sharePatient(patientId: String, caregiverId: String) {
        val isConnected = _uiState.value.connections.connected
            .any { it.caregiverId == caregiverId }

        if (!isConnected) {
            _uiState.update {
                it.copy(errorMessage = "Connect with this caregiver first.")
            }
            return
        }

        performAction {
            repository.sharePatient(patientId, caregiverId)
        }
    }

    private fun performAction(action: suspend () -> Unit) {
        if (_uiState.value.isActionRunning) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isActionRunning = true, errorMessage = null)
            }

            try {
                action()

                val (connections, patients) = loadData()
                _uiState.update {
                    it.copy(
                        connections = connections,
                        patients = patients
                    )
                }

                val query = _uiState.value.searchQuery
                if (query.isNotBlank()) {
                    val results = repository.searchCaregivers(query.trim())
                    _uiState.update {
                        it.copy(searchResults = results)
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Unable to complete sharing action.")
                }
            } finally {
                _uiState.update { it.copy(isActionRunning = false) }
            }
        }
    }

    private suspend fun loadData() = coroutineScope {
        val connections = async { repository.getConnections() }
        val patients = async { repository.getAccessiblePatients() }

        connections.await() to patients.await()
    }
}