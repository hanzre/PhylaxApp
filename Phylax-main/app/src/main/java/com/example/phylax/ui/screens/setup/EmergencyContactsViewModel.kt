package com.example.phylax.ui.screens.setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phylax.data.model.EmergencyContact
import com.example.phylax.data.repository.EmergencyRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EmergencyContactsViewModel(
    private val repository: EmergencyRepository = EmergencyRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    var uiState by mutableStateOf(ContactsUiState())
        private set

    init {
        loadContacts()
    }

    fun loadContacts() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            repository.getContacts(userId).onSuccess { contacts ->
                uiState = uiState.copy(contacts = contacts, isLoading = false)
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun addContact(name: String, phone: String) {
        val userId = auth.currentUser?.uid ?: run {
            uiState = uiState.copy(error = "User not logged in")
            return
        }

        // Ensure the number is exactly 11 digits (e.g., 09171234567)
        if (phone.length != 11 || !phone.startsWith("0")) {
            uiState = uiState.copy(error = "Please enter a valid 11-digit PH mobile number")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val newContact = EmergencyContact(
                userId = userId,
                name = name,
                phone = phone, // Storing as 11-digit format for display
                isPrimary = uiState.contacts.isEmpty()
            )
            repository.addContact(newContact).onSuccess {
                loadContacts()
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            repository.deleteContact(contactId).onSuccess {
                loadContacts()
            }.onFailure {
                uiState = uiState.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun setPrimaryContact(contactId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            // 1. Unset old primary
            val oldPrimary = uiState.contacts.find { it.isPrimary }
            oldPrimary?.let {
                repository.updateContact(it.copy(isPrimary = false)).onFailure {
                    uiState = uiState.copy(isLoading = false, error = it.message)
                }
            }
            // 2. Set new primary
            val newPrimary = uiState.contacts.find { it.id == contactId }
            newPrimary?.let {
                repository.updateContact(it.copy(isPrimary = true)).onSuccess {
                    loadContacts()
                }.onFailure {
                    uiState = uiState.copy(isLoading = false, error = it.message)
                }
            } ?: run {
                loadContacts()
            }
        }
    }
}

data class ContactsUiState(
    val contacts: List<EmergencyContact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
