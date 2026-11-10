package com.example.phylax.ui.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.phylax.ui.components.PremiumButton
import com.example.phylax.ui.theme.*

@Composable
fun PermissionScreen(
    onAllow: () -> Unit,
    onNotNow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = MaterialTheme.shapes.large,
            color = LocationBlue.copy(alpha = 0.1f)
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.padding(24.dp).size(64.dp),
                tint = LocationBlue
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "Share Your Location When You Need Help",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = PrimaryText,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Phylax can share your current and live location with your emergency contacts when you activate an SOS alert.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = SecondaryText,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        PremiumButton(
            text = "ALLOW LOCATION",
            onClick = onAllow
        )
        
        TextButton(onClick = onNotNow) {
            Text("NOT NOW", color = SecondaryText)
        }
    }
}

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        content = content
    )
}
