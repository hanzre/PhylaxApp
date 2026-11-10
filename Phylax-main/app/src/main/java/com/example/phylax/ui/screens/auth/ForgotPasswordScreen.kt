package com.example.phylax.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.phylax.ui.components.PremiumButton
import com.example.phylax.ui.components.PremiumTextField
import com.example.phylax.ui.theme.Background
import com.example.phylax.ui.theme.PrimaryText

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.PasswordResetSent -> {
                    snackbarHostState.showSnackbar("Password reset link sent to your email")
                }
                is AuthEvent.Error -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = PrimaryText)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Forgot Password",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = PrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Text(
                text = "Enter your email to receive a password reset link",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            PremiumTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PremiumButton(
                text = "SEND RESET LINK",
                onClick = {
                    if (email.isNotEmpty()) {
                        viewModel.onEvent(AuthUserEvent.ForgotPassword(email))
                    }
                },
                enabled = !viewModel.uiState.isLoading
            )
            
            if (viewModel.uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp))
            }
        }
    }
}
