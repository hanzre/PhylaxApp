package com.example.phylax.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phylax.ui.theme.*

@Composable
fun SafetyTipsScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = PrimaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Safety Tips",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        safetyTips.forEach { tip ->
            SafetyTipCard(tip)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SafetyTipCard(tip: SafetyTip) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = VioletAccent)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = tip.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(text = tip.content, style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText))
        }
    }
}

data class SafetyTip(val title: String, val content: String)

val safetyTips = listOf(
    SafetyTip("Walking alone at night", "Stay in well-lit areas and avoid shortcuts through dark alleys or parks. Keep your head up and stay aware of your surroundings."),
    SafetyTip("Public transport safety", "Wait for buses or trains in well-lit areas near other people. Sit near the driver or in a carriage with other passengers."),
    SafetyTip("Stay aware of surroundings", "Avoid using headphones or looking at your phone for long periods while walking. Trust your instincts if a situation feels unsafe.")
)
