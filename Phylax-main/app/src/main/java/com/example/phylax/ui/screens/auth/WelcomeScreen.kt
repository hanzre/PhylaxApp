package com.example.phylax.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phylax.ui.components.GlassCard
import com.example.phylax.ui.components.GlowButton
import com.example.phylax.ui.components.PhylaxShield
import com.example.phylax.ui.theme.*

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PhylaxShield(size = 160.dp)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Phylax",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = PrimaryText,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp
                    )
                )

                Text(
                    text = "Vision 2027",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = SecondaryText,
                        letterSpacing = 4.sp
                    )
                )
            }

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Intelligent Security",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = PrimaryText,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Next-generation emergency response at your fingertips.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = SecondaryText,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    GlowButton(
                        text = "Explore",
                        onClick = onGetStarted,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    PhylaxTheme {
        WelcomeScreen(onGetStarted = {})
    }
}
