package com.example.phylax.ui.screens.setup

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phylax.data.model.EmergencyContact
import com.example.phylax.ui.components.PremiumButton
import com.example.phylax.ui.theme.*

@Composable
fun EmergencyContactsScreen(
    onContinue: () -> Unit,
    onBack: (() -> Unit)? = null,
    viewModel: EmergencyContactsViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding)
                .padding(24.dp)
        ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = PrimaryText)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = "Emergency Contacts",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        Text(
            text = "Alert these people when you need help",
            style = MaterialTheme.typography.bodyLarge.copy(color = SecondaryText)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (uiState.isLoading && uiState.contacts.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LocationBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.contacts) { contact ->
                    ContactCard(
                        contact = contact, 
                        onDelete = { viewModel.deleteContact(contact.id.orEmpty()) },
                        onSetPrimary = { viewModel.setPrimaryContact(contact.id.orEmpty()) },
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                            context.startActivity(intent)
                        }
                    )
                }
                
                item {
                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LocationBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ADD EMERGENCY CONTACT")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        PremiumButton(
            text = "CONTINUE",
            onClick = onContinue,
            enabled = uiState.contacts.isNotEmpty() && !uiState.isLoading
        )

        TextButton(
            onClick = onContinue,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = !uiState.isLoading
        ) {
            Text("SKIP FOR NOW", color = SecondaryText, fontWeight = FontWeight.SemiBold)
        }
    }

        if (showAddDialog) {
            AddContactDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, phone ->
                    viewModel.addContact(name, phone)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ContactCard(
    contact: EmergencyContact, 
    onDelete: () -> Unit,
    onSetPrimary: () -> Unit,
    onCall: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = Background
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(12.dp), tint = SecondaryText)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = contact.name, style = MaterialTheme.typography.titleLarge.copy(color = PrimaryText))
                        if (contact.isPrimary) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(containerColor = SuccessGreen.copy(alpha = 0.1f)) {
                                Text("PRIMARY", color = SuccessGreen, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    Text(text = contact.phone, style = MaterialTheme.typography.bodyLarge.copy(color = SecondaryText))
                }
                
IconButton(onClick = onCall) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = LocationBlue)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderColor.copy(alpha = 0.5f))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (!contact.isPrimary) {
                    TextButton(onClick = onSetPrimary) {
                        Text("SET AS PRIMARY", color = LocationBlue)
                    }
                }
                TextButton(onClick = onDelete) {
                    Text("REMOVE", color = EmergencyRed)
                }
            }
        }
    }
}

@Composable
fun AddContactDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Contact") },
        text = {
            Column {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Name") },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = phone, 
                    onValueChange = { 
                        if (it.length <= 11 && it.all { char -> char.isDigit() }) {
                            phone = it 
                        }
                    }, 
                    label = { Text("Phone Number") },
                    placeholder = { Text("e.g. 09171234567") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onAdd(name, phone) }) { Text("ADD") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        }
    )
}
