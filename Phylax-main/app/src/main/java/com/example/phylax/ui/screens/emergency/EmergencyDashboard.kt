package com.example.phylax.ui.screens.emergency

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.phylax.ui.theme.*
import com.example.phylax.service.SOSService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun EmergencyDashboard(
    userId: String,
    onStopSOS: () -> Unit,
    onViewMap: () -> Unit
) {
    val context = LocalContext.current
    var timerSeconds by remember { mutableIntStateOf(0) }
    val timerJob = remember { Job() }
    val timerScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val intent = Intent(context, SOSService::class.java).apply {
            action = SOSService.ACTION_START_SOS
            putExtra(SOSService.EXTRA_USER_ID, userId)
        }
        ContextCompat.startForegroundService(context, intent)

        timerJob.invokeOnCompletion {
            // timerScope cancelled when job completes
        }
        
        timerScope.launch(timerJob) {
            while (true) {
                delay(1000)
                timerSeconds++
            }
        }
    }

    val minutes = timerSeconds / 60
    val seconds = timerSeconds % 60
    val timeString = "%02d:%02d".format(minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(iOSRed)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())

        Text(
            text = "SOS ALERT ACTIVE",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        
        Text(
            text = timeString,
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            EmergencyStatusRow(
                title = "Live Location",
                status = "Sharing",
                icon = Icons.Default.ShareLocation
            )
            EmergencyStatusRow(
                title = "Emergency Siren",
                status = "Active",
                icon = Icons.Default.NotificationsActive
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            EmergencyActionButton(
                text = "Call Emergency (911)",
                icon = Icons.Default.Phone,
                color = Color.White,
                contentColor = iOSRed,
                onClick = {
                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
                    callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(callIntent)
                }
            )
            
            EmergencyActionSquare(
                icon = Icons.Default.Map,
                label = "Map",
                onClick = onViewMap,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable {
                        val stopIntent = Intent(context, SOSService::class.java).apply {
                            action = SOSService.ACTION_STOP_SOS
                        }
                        context.startService(stopIntent)
                        timerJob.cancel()
                        onStopSOS()
                    },
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "STOP EMERGENCY SESSION",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
fun EmergencyStatusRow(title: String, status: String, icon: ImageVector) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, color = Color.White, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = status, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun EmergencyActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(60.dp),
        shape = RoundedCornerShape(18.dp),
        color = color,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = contentColor)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, color = contentColor, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun EmergencyActionSquare(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Text(text = label, color = Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}
