package com.example.phylax.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phylax.data.model.EmergencyContact
import com.example.phylax.data.model.User
import com.example.phylax.data.repository.EmergencyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

data class MessageContact(
    val contact: EmergencyContact,
    val user: User?
) {
    val displayName: String
        get() = contact.name.trim()
            .ifBlank { user?.name?.trim().orEmpty() }
            .ifBlank { "Emergency Contact" }
}

data class MessagesUiState(
    val contacts: List<MessageContact> = emptyList(),
    val isLoading: Boolean = false,
    val hasLoaded: Boolean = false,
    val errorMessage: String? = null
)

class MessagesViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val emergencyRepository: EmergencyRepository = EmergencyRepository(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    var uiState by mutableStateOf(MessagesUiState())
        private set

    private var loadedUserId: String? = null

    /**
     * Loads the emergency-contact list only once per signed-in user. The
     * ViewModel survives navigation to a chat, so returning to Messages does
     * not trigger another load or flash the loading screen.
     */
    fun loadIfNeeded() {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            uiState = MessagesUiState(
                isLoading = false,
                hasLoaded = true,
                errorMessage = "Please log in to view messages"
            )
            return
        }

        if (loadedUserId == userId && (uiState.hasLoaded || uiState.isLoading)) return
        load(userId)
    }

    fun retry() {
        val userId = auth.currentUser?.uid ?: return
        loadedUserId = null
        load(userId)
    }

    private fun load(userId: String) {
        loadedUserId = userId
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val contacts = withTimeout(15_000) {
                    emergencyRepository.getContacts(userId).getOrThrow()
                }

                if (contacts.isEmpty()) {
                    uiState = MessagesUiState(isLoading = false, hasLoaded = true)
                    return@launch
                }

                val users = withTimeout(15_000) {
                    firestore.collection("users")
                        .get()
                        .await()
                        .documents
                        .mapNotNull { document ->
                            // Older profiles may omit the uid field. The
                            // document ID is the real Firebase Auth UID.
                            document.toObject(User::class.java)?.copy(uid = document.id)
                        }
                }

                val usersByPhone = users
                    .filter { it.uid != userId && normalizePhone(it.phone).isNotBlank() }
                    .groupBy { normalizePhone(it.phone) }
                    .mapValues { (_, matchingUsers) -> matchingUsers.first() }

                val contactChats = contacts
                    .filter { normalizePhone(it.phone).isNotBlank() }
                    .distinctBy { normalizePhone(it.phone) }
                    .map { contact ->
                        MessageContact(
                            contact = contact,
                            user = usersByPhone[normalizePhone(contact.phone)]
                        )
                    }
                    .sortedWith(
                        compareByDescending<MessageContact> { it.user != null }
                            .thenByDescending { it.contact.isPrimary }
                            .thenBy { it.displayName.lowercase() }
                    )

                uiState = MessagesUiState(
                    contacts = contactChats,
                    isLoading = false,
                    hasLoaded = true
                )

                Log.d(
                    "MessagesViewModel",
                    "Loaded ${contacts.size} emergency contacts; " +
                        "${contactChats.count { it.user != null }} matched to Phylax users"
                )
            } catch (e: Exception) {
                uiState = MessagesUiState(
                    isLoading = false,
                    hasLoaded = true,
                    errorMessage = "Failed to load messages: ${e.message}"
                )
            }
        }
    }

    /**
     * Converts common PH mobile formats to the 11-digit local format used by
     * emergency contacts, e.g. +63 917 123 4567 -> 09171234567.
     */
    private fun normalizePhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return when {
            digits.length == 12 && digits.startsWith("63") -> "0" + digits.substring(2)
            digits.length == 10 && digits.startsWith("9") -> "0$digits"
            else -> digits
        }
    }
}
