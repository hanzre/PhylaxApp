package com.example.phylax.data.repository

import com.example.phylax.data.model.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class ChatRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getChatId(user1: String, user2: String): String {
        return if (user1 < user2) "${user1}_${user2}" else "${user2}_${user1}"
    }

    fun getMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val subscription = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error by closing the flow with exception
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages = snapshot.toObjects(ChatMessage::class.java)
                    trySend(messages)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun sendMessage(chatId: String, message: ChatMessage): Result<String> {
        return try {
            val messageId = withTimeout(10000) {
                val doc = firestore.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .document()
                doc.set(message.copy(id = doc.id)).await()
                doc.id
            }
            Result.success(messageId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
