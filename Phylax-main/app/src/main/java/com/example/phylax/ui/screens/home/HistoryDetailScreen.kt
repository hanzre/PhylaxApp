package com.example.phylax.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phylax.data.model.EmergencySession
import com.example.phylax.data.repository.EmergencyRepository
import com.example.phylax.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryDetailScreen(
    sessionId: String,
    onBack: () -> Unit
) {
    var session by remember { mutableStateOf<EmergencySession?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val auth = FirebaseAuth.getInstance()
    val repository = EmergencyRepository()
    val context = LocalContext.current

    LaunchedEffect(sessionId) {
        if (sessionId.isNotEmpty()) {
            isLoading = true
            val userId = auth.currentUser?.uid ?: ""
            if (userId.isNotEmpty()) {
                val result = repository.getEmergencyHistory(userId).getOrNull()
                session = result?.find { it.id == sessionId }
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = PrimaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Alert Details",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LocationBlue)
            }
        } else {
            session?.let { session ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(56.dp),
                                shape = MaterialTheme.shapes.medium,
                                color = EmergencyRed.copy(alpha = 0.1f)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.padding(16.dp), tint = EmergencyRed)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = if (session.note.isNotEmpty()) session.note else "Emergency Alert", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                                val dateFormat = SimpleDateFormat("MMM d — h:mm a", Locale.getDefault())
                                Text(text = dateFormat.format(Date(session.startedAt)), style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText))
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), color = BorderColor.copy(alpha = 0.5f))

                        DetailItem(label = "Status", value = session.status, valueColor = if (session.status == "ENDED") SuccessGreen else EmergencyRed)
                        DetailItem(label = "Latitude", value = session.latitude.toString())
                        DetailItem(label = "Longitude", value = session.longitude.toString())
                        if (session.endedAt != null) {
                            val duration = (session.endedAt!! - session.startedAt) / 1000 / 60
                            DetailItem(label = "Duration", value = "$duration minutes")
                        }
                        if (session.note.isNotEmpty()) {
                            DetailItem(label = "Note", value = session.note)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (session.latitude != 0.0 && session.longitude != 0.0) {
                                    val gmmIntentUri = Uri.parse("geo:${session.latitude},${session.longitude}?q=${session.latitude},${session.longitude}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(containerColor = LocationBlue),
                            enabled = session.latitude != 0.0 && session.longitude != 0.0
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (session.latitude != 0.0 && session.longitude != 0.0) "VIEW LAST LOCATION" else "No Location Data",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } ?: Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Session not found", color = SecondaryText)
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, valueColor: Color = PrimaryText) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
        Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(color = valueColor, fontWeight = FontWeight.SemiBold))
    }
}
