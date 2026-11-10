package com.example.phylax.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.phylax.R

@Composable
fun PhylaxShield(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Use the uploaded custom icon
        Image(
            painter = painterResource(id = R.drawable.logo_ng_app_namin),
            contentDescription = "Phylax Logo",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(size * 0.25f))
        )
    }
}
