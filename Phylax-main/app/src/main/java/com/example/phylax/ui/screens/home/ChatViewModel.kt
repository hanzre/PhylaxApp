package com.example.phylax.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phylax.data.model.ChatMessage
import com.example.phylax.data.repository.ChatRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ChatUiState())
        private set

    private var currentChatId: String? = null

    fun initChat(currentUserId: String, otherUserId: String) {
        if (currentUserId.isBlank() || otherUserId.isBlank()) {
            uiState = uiState.copy(
                isLoading = false,
                errorMessage = "Unable to open chat: missing user information"
            )
            return
        }

        val chatId = repository.getChatId(currentUserId, otherUserId)
        currentChatId = chatId

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                repository.getMessages(chatId).collectLatest { messages ->
                    uiState = uiState.copy(messages = messages, isLoading = false)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Unable to load messages: ${e.message}"
                )
            }
        }
    }

    fun sendMessage(currentUserId: String, text: String, otherUserId: String) {
        val chatId = currentChatId ?: run {
            uiState = uiState.copy(errorMessage = "Chat is not ready yet. Please reopen it.")
            return
        }

        val trimmedText = text.trim()
        if (trimmedText.isEmpty()) return

        val message = ChatMessage(
            senderId = currentUserId,
            receiverId = otherUserId,
            text = trimmedText
        )

        viewModelScope.launch {
            val result = repository.sendMessage(chatId, message)
            result.onSuccess {
                uiState = uiState.copy(errorMessage = null)
            }.onFailure {
                uiState = uiState.copy(errorMessage = "Message not sent: ${it.message}")
            }
        }
    }

    fun consumeError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
