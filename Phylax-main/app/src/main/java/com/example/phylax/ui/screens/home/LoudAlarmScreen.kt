package com.example.phylax.ui.screens.home

import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phylax.ui.components.PremiumButton
import com.example.phylax.ui.theme.*

@Composable
fun LoudAlarmScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    LaunchedEffect(Unit) {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        mediaPlayer = MediaPlayer.create(context, alarmUri).apply {
            isLooping = true
            start()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "alarm")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Box(contentAlignment = Alignment.Center) {
            // Animated Waveform Rings
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale)
                    .background(color = iOSRed.copy(alpha = 0.1f), shape = CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale * 0.8f)
                    .background(color = iOSRed.copy(alpha = 0.2f), shape = CircleShape)
            )

            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = EmergencyRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "ALARM IS ON",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = EmergencyRed,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        )

        Text(
            text = "Playing loud emergency sound",
            style = MaterialTheme.typography.bodyLarge.copy(color = SecondaryText)
        )

        Spacer(modifier = Modifier.height(120.dp))

        PremiumButton(
            text = "STOP ALARM",
            onClick = onBack,
            isEmergency = true
        )
    }
}
