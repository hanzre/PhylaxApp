package com.example.phylax.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phylax.data.model.EmergencySession
import com.example.phylax.data.repository.EmergencyRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: EmergencyRepository = EmergencyRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    var uiState by mutableStateOf(HistoryUiState())
        private set

    init {
        loadHistory()
    }

    fun loadHistory() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            repository.getEmergencyHistory(userId).onSuccess { sessions ->
                uiState = uiState.copy(sessions = sessions, isLoading = false)
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun updateNote(sessionId: String, note: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            repository.updateSessionNote(sessionId, note).onSuccess {
                loadHistory()
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun deleteRecord(sessionId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            repository.deleteSession(sessionId).onSuccess {
                loadHistory()
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = it.message)
            }
        }
    }
}

data class HistoryUiState(
    val sessions: List<EmergencySession> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
