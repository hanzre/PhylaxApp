package com.example.phylax.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phylax.data.model.ChatMessage
import com.example.phylax.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    otherUserId: String,
    otherUserName: String,
    onBack: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    var messageText by remember { mutableStateOf("") }
    // The Firebase session UID is authoritative; profile documents may have
    // been written by older versions without a uid field.
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    LaunchedEffect(key1 = otherUserId, key2 = currentUserId) {
        if (currentUserId.isNotBlank()) {
            viewModel.initChat(currentUserId, otherUserId)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.consumeError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(otherUserName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            ChatInput(
                text = messageText,
                onTextChange = { messageText = it },
                onSend = {
                    if (messageText.isNotEmpty() && currentUserId.isNotBlank()) {
                        viewModel.sendMessage(currentUserId, messageText, otherUserId)
                        messageText = ""
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(uiState.messages) { message ->
                MessageBubble(message = message, isCurrentUser = message.senderId == currentUserId)
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage, isCurrentUser: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isCurrentUser) LocationBlue else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isCurrentUser) 16.dp else 0.dp,
                bottomEnd = if (isCurrentUser) 0.dp else 16.dp
            ),
            shadowElevation = 2.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = if (isCurrentUser) Color.White else PrimaryText,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun ChatInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LocationBlue,
                    unfocusedBorderColor = BorderColor
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSend,
                colors = IconButtonDefaults.iconButtonColors(containerColor = LocationBlue, contentColor = Color.White)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}
