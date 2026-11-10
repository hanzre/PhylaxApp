package com.example.phylax.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phylax.ui.components.GlassCard
import com.example.phylax.ui.components.SOSButton
import com.example.phylax.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToSOS: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAlarm: () -> Unit,
    onNavigateToLocation: () -> Unit,
    onNavigateToMessages: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Mesh background effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(MeshBlue.copy(alpha = 0.1f), Color.Transparent),
                        radius = 2000f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            
            // iOS 27 Futuristic Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Phylax",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = PrimaryText,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = "Vision 2027 Active",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = iOSBlue,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
                
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GlassBackground)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryText)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Status Monitor (Glass Card)
            StatusMonitor()
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // SOS Center
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SOSButton(
                    onActivated = onNavigateToSOS
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Modular Control Center
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassActionCard(
                    title = "Alarm", 
                    subtitle = "High Frequency", 
                    icon = Icons.Default.Notifications, 
                    color = iOSRed, 
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAlarm
                )
                GlassActionCard(
                    title = "Live", 
                    subtitle = "Deep Tracking", 
                    icon = Icons.Default.Place, 
                    color = iOSBlue, 
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLocation
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            GlassActionCardLarge(
                title = "Emergency Guardians",
                subtitle = "Manage secure contacts",
                icon = Icons.Default.Group,
                color = iOSIndigo,
                onClick = onNavigateToContacts
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            GlassActionCardLarge(
                title = "Messages",
                subtitle = "Chat with other Phylax users",
                icon = Icons.Default.ChatBubble,
                color = iOSGreen,
                onClick = onNavigateToMessages
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Futuristic Security Insight
            SecurityInsightCard()
            
            Spacer(modifier = Modifier.height(40.dp).navigationBarsPadding())
        }
    }
}

@Composable
fun StatusMonitor() {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(iOSGreen, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Quantum Shield Active",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "100%",
                style = MaterialTheme.typography.labelMedium.copy(color = iOSGreen, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun GlassActionCard(
    title: String, 
    subtitle: String, 
    icon: ImageVector, 
    color: Color, 
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(120.dp),
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        onClick = onClick
    ) {
        GlassCard(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = color)
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp))
                    Text(text = subtitle, style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
                }
            }
        }
    }
}

@Composable
fun GlassActionCardLarge(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        onClick = onClick
    ) {
        GlassCard(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = color)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp))
                    Text(text = subtitle, style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = SecondaryText.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun SecurityInsightCard() {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Security Analysis",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SecurityCheckItem("Encrypted Mesh Active")
            SecurityCheckItem("Location spoofing disabled")
            SecurityCheckItem("Neural Alert ready")
        }
    }
}

@Composable
fun SecurityCheckItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = iOSGreen,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PhylaxTheme {
        HomeScreen(
            onNavigateToSOS = {},
            onNavigateToContacts = {},
            onNavigateToHistory = {},
            onNavigateToSettings = {},
            onNavigateToAlarm = {},
            onNavigateToLocation = {},
            onNavigateToMessages = {}
        )
    }
}
