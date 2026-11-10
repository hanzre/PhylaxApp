package com.example.phylax.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phylax.ui.theme.Background
import com.example.phylax.ui.theme.PrimaryText
import com.example.phylax.ui.theme.SecondaryText
import com.example.phylax.ui.screens.auth.AuthViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel
) {
    val user = viewModel.uiState.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = PrimaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Profile Information",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = SecondaryText)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ProfileItem(label = "Full Name", value = user?.name ?: "N/A")
        ProfileItem(label = "Phone Number", value = user?.phone ?: "N/A")
        ProfileItem(label = "Email Address", value = user?.email ?: "N/A")
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium.copy(color = SecondaryText))
        Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(color = PrimaryText, fontWeight = FontWeight.SemiBold))
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))
    }
}
