package com.example.phylax.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.phylax.ui.theme.GlassBackground
import com.example.phylax.ui.theme.GlassBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(GlassBackground)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassBorder,
                        Color.Transparent
                    )
                ),
                shape = MaterialTheme.shapes.large
            )
    ) {
        // Subtle background blur simulation (real blur would be on the background behind)
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(30.dp)
        )
        
        Box(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}
