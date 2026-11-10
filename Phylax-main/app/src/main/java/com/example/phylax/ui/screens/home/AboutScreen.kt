package com.example.phylax.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phylax.ui.components.PhylaxShield
import com.example.phylax.ui.theme.*

@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = iOSBlue)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "About",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PhylaxShield(size = 120.dp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Phylax",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryText
                )
            )
            
            Text(
                text = "Version 1.0.0 (Prototype)",
                style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Mission",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Phylax was built to provide peace of mind and immediate assistance in critical situations. Our mission is to bridge the gap between users and their guardians during emergencies using modern mobile technology.",
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                        color = SecondaryText
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Key Features",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureInfoRow("One-Tap SOS Alerting")
                    FeatureInfoRow("Live Location Sharing")
                    FeatureInfoRow("Real-Time Messaging")
                    FeatureInfoRow("Emergency History Logging")
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "© 2026 Phylax Safety. All rights reserved.",
                style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun FeatureInfoRow(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(iOSBlue, RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge.copy(color = SecondaryText))
    }
}
