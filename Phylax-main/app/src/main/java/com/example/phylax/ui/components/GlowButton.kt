package com.example.phylax.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phylax.ui.theme.iOSBlue
import com.example.phylax.ui.theme.iOSBlueGlow

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = iOSBlue,
    glowColor: Color = iOSBlueGlow
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = glowColor,
                spotColor = glowColor
            )
            .clip(CircleShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        containerColor,
                        containerColor.copy(alpha = 0.9f)
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
