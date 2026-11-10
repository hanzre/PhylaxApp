package com.example.phylax.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.phylax.ui.theme.Background
import com.example.phylax.ui.theme.EmergencyRed
import com.example.phylax.ui.theme.LocationBlue
import com.example.phylax.ui.theme.PrimaryText
import com.example.phylax.ui.theme.SecondaryText
import com.example.phylax.ui.theme.iOSBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    navController: NavHostController,
    onNavigateToChat: (String, String) -> Unit,
    viewModel: MessagesViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadIfNeeded()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = iOSBlue)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Messages",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Text(
            text = "Only your emergency contacts are shown here.",
            style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        if (!uiState.hasLoaded || uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LocationBlue)
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = EmergencyRed.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Error",
                        style = MaterialTheme.typography.titleMedium.copy(color = PrimaryText)
                    )
                    Text(
                        uiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.retry() }) {
                        Text("Retry", color = iOSBlue)
                    }
                }
            }
        } else if (uiState.contacts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SecondaryText.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No emergency contacts yet",
                        style = MaterialTheme.typography.titleMedium.copy(color = SecondaryText)
                    )
                    Text(
                        "Add an emergency contact in Settings to message them here.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText.copy(alpha = 0.7f))
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                items(
                    items = uiState.contacts,
                    key = { it.contact.id?.takeIf(String::isNotBlank) ?: it.contact.phone }
                ) { messageContact ->
                    MessageContactItem(
                        messageContact = messageContact,
                        onClick = {
                            messageContact.user?.let { user ->
                                onNavigateToChat(user.uid, messageContact.displayName)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageContactItem(
    messageContact: MessageContact,
    onClick: () -> Unit
) {
    val canChat = messageContact.user != null
    val accentColor = if (canChat) LocationBlue else SecondaryText

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canChat, onClick = onClick)
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(shape = CircleShape, color = accentColor.copy(alpha = 0.1f)) {
                    Text(
                        text = messageContact.displayName.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = messageContact.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (canChat) PrimaryText else SecondaryText,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = when {
                        !canChat -> "Not on Phylax yet"
                        messageContact.contact.isPrimary -> "Primary emergency contact"
                        else -> "Emergency contact"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                )
            }

            if (canChat) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = SecondaryText.copy(alpha = 0.5f)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "This contact does not have a Phylax account",
                    tint = SecondaryText.copy(alpha = 0.4f)
                )
            }
        }
    }
}
