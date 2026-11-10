package com.example.phylax.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phylax.data.model.EmergencySession
import com.example.phylax.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onRecordClick: (EmergencySession) -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    var sessionToEdit by remember { mutableStateOf<EmergencySession?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())
        
        // iOS 18 Style Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = iOSBlue)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "History",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
        
        if (uiState.isLoading && uiState.sessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = iOSBlue)
            }
        } else if (uiState.sessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No alert history", color = SecondaryText)
            }
        } else {
            // iOS Inset Grouped Style for the list
            Surface(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f))
            ) {
                LazyColumn {
                    itemsIndexed(uiState.sessions) { index, session ->
                        HistoryItem(
                            session = session, 
                            onClick = { onRecordClick(session) },
                            onEditNote = { sessionToEdit = session },
                            onDelete = { viewModel.deleteRecord(session.id.orEmpty()) },
                            isLast = index == uiState.sessions.size - 1
                        )
                    }
                }
            }
        }
    }

    sessionToEdit?.let { session ->
        EditNoteDialog(
            initialNote = session.note,
            onDismiss = { sessionToEdit = null },
            onConfirm = { newNote ->
                viewModel.updateNote(session.id.orEmpty(), newNote)
                sessionToEdit = null
            }
        )
    }
}

@Composable
fun HistoryItem(
    session: EmergencySession, 
    onClick: () -> Unit,
    onEditNote: () -> Unit,
    onDelete: () -> Unit,
    isLast: Boolean
) {
    var showMenu by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM d — h:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(session.startedAt))

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = iOSRed.copy(alpha = 0.1f)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.padding(8.dp), tint = iOSRed)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (session.note.isNotEmpty()) session.note else "Emergency Alert", 
                    style = MaterialTheme.typography.bodyLarge.copy(color = PrimaryText, fontWeight = FontWeight.SemiBold)
                )
                Text(text = dateString, style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
            }
            
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreHoriz, contentDescription = "Menu", tint = Color.LightGray)
            }
            
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit Note") },
                    onClick = {
                        showMenu = false
                        onEditNote()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = iOSRed) },
                    onClick = {
                        showMenu = false
                        onDelete()
                    }
                )
            }
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp),
                color = Color.Black.copy(alpha = 0.05f)
            )
        }
    }
}

@Composable
fun EditNoteDialog(
    initialNote: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var note by remember { mutableStateOf(initialNote) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Alert Note") },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Title / Note") },
                placeholder = { Text("e.g., Accident at Main St") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(note) }) {
                Text("SAVE", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}
