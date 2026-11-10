package com.example.phylax.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.phylax.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToSafetySetup: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
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
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // iOS 18 Inset Grouped Sections
        SettingsSection(title = "ACCOUNT") {
            SettingsItem(title = "Profile Information", icon = Icons.Default.Person, onClick = onNavigateToProfile)
            SettingsItem(title = "Phone Number", icon = Icons.Default.Phone, onClick = onNavigateToProfile, isLast = true)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SettingsSection(title = "SAFETY") {
            SettingsItem(title = "Emergency Contacts", icon = Icons.Default.Group, onClick = onNavigateToContacts)
            SettingsItem(title = "SOS Settings", icon = Icons.Default.Sos, onClick = onNavigateToSafetySetup)
            SettingsItem(title = "Permissions", icon = Icons.Default.Lock, onClick = onNavigateToPermissions, isLast = true)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SettingsSection(title = "OTHER") {
            SettingsItem(title = "About Phylax", icon = Icons.Default.Info, onClick = onNavigateToAbout)
            SettingsItem(
                title = "Log Out", 
                icon = Icons.AutoMirrored.Filled.Logout, 
                tint = iOSRed, 
                onClick = { showLogoutDialog = true },
                isLast = true
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showLogoutDialog) {
        // iOS Style Alert Dialog
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = { Text("Are you sure you want to log out?", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Out", color = iOSRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel", color = iOSBlue)
                }
            },
            shape = RoundedCornerShape(14.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText, fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f))
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsItem(
    title: String, 
    icon: ImageVector, 
    onClick: () -> Unit, 
    tint: Color = PrimaryText,
    isLast: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = if (tint == PrimaryText) iOSBlue else tint)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(color = tint))
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.LightGray)
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 54.dp),
                color = Color.Black.copy(alpha = 0.05f)
            )
        }
    }
}
