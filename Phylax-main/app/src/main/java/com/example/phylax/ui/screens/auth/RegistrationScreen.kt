package com.example.phylax.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.phylax.data.model.User
import com.example.phylax.ui.components.PremiumButton
import com.example.phylax.ui.components.PremiumTextField
import com.example.phylax.ui.theme.*

@Composable
fun RegistrationScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: AuthViewModel
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.AuthSuccess -> onRegisterSuccess()
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
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = PrimaryText,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            
            Text(
                text = "Join Phylax to stay protected",
                style = MaterialTheme.typography.bodyLarge.copy(color = SecondaryText)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // iOS Style Stacked Inputs
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PremiumTextField(value = name, onValueChange = { name = it }, label = "Full Name", icon = Icons.Default.Person)
                PremiumTextField(
                    value = phone, 
                    onValueChange = { 
                        if (it.length <= 11 && it.all { char -> char.isDigit() }) {
                            phone = it 
                        }
                    }, 
                    label = "Phone Number", 
                    icon = Icons.Default.Phone, 
                    keyboardType = KeyboardType.Phone
                )
                PremiumTextField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
                
                PremiumTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )
                
                PremiumTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            PremiumButton(
                text = "Create Account",
                onClick = {
                    if (name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        if (password == confirmPassword) {
                            val user = User(name = name, phone = phone, email = email)
                            viewModel.onEvent(AuthUserEvent.RegisterWithEmail(user, password))
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Passwords do not match") }
                        }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Please fill in all fields") }
                    }
                },
                enabled = !viewModel.uiState.isLoading
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("ALREADY HAVE AN ACCOUNT? LOG IN", color = iOSBlue, fontWeight = FontWeight.Bold)
            }
            
            if (viewModel.uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = iOSBlue)
            }
        }
    }
}
