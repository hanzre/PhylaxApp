package com.example.phylax.ui.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phylax.ui.components.PremiumButton
import com.example.phylax.ui.theme.*

@Composable
fun SafetySettingsScreen(
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Text(
            text = "Customize Your Safety",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = PrimaryText,
                fontWeight = FontWeight.Bold
            )
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SafetySettingItem(
            title = "Emergency SOS",
            description = "Automatically send an emergency alert to selected contacts.",
            icon = Icons.Default.Sos
        )
        
        SafetySettingItem(
            title = "Live Location",
            description = "Share your real-time location during an active emergency.",
            icon = Icons.Default.ShareLocation
        )
        
        SafetySettingItem(
            title = "Loud Alarm",
            description = "Play a loud alarm sound when SOS is activated.",
            icon = Icons.Default.NotificationsActive
        )
        
        SafetySettingItem(
            title = "Fake Call",
            description = "Optional feature that simulates an incoming call.",
            icon = Icons.Default.PhoneInTalk
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        PremiumButton(
            text = "FINISH SETUP",
            onClick = onFinish
        )
    }
}

@Composable
fun SafetySettingItem(
    title: String,
    description: String,
    icon: ImageVector
) {
    var checked by remember { mutableStateOf(true) }
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.small,
                color = Background
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(8.dp), tint = PrimaryText)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(color = PrimaryText, fontWeight = FontWeight.SemiBold))
                Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText))
            }
            
            Switch(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = LocationBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}
