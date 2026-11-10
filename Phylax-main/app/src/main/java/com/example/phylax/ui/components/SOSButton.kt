package com.example.phylax.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phylax.ui.theme.iOSRed

@Composable
fun SOSButton(
    onActivated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .size(260.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
                    onActivated()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // iOS 18 Style Glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(pulseScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(iOSRed.copy(alpha = 0.15f), Color.Transparent),
                        radius = 400f
                    ),
                    CircleShape
                )
        )

        // Static Ring (iOS style)
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            drawCircle(
                color = iOSRed.copy(alpha = 0.2f),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Main Button (Glass Effect)
        Surface(
            modifier = Modifier.size(190.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 12.dp,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "SOS",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = iOSRed,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}
