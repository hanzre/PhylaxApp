package com.example.phylax.ui.screens.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.phylax.ui.components.PremiumButton
import com.example.phylax.ui.components.PremiumTextField
import com.example.phylax.ui.theme.*

import androidx.compose.ui.unit.sp
import com.example.phylax.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.AuthSuccess -> onLoginSuccess()
                is AuthEvent.Error -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            
            Text(
                text = "Sign in to your Phylax account",
                style = MaterialTheme.typography.bodyLarge.copy(color = SecondaryText)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PremiumTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                
                PremiumTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )
            }

            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?", color = iOSBlue, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PremiumButton(
                text = "Log In",
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        viewModel.onEvent(AuthUserEvent.LoginWithEmail(email, password))
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Please enter both email and password") }
                    }
                },
                enabled = !viewModel.uiState.isLoading
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onRegisterClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("NEW TO PHYLAX? CREATE ACCOUNT", color = iOSBlue, fontWeight = FontWeight.Bold)
            }
            
            if (viewModel.uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = iOSBlue)
            }
        }
    }
}
