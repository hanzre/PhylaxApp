package com.example.phylax.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.phylax.ui.theme.Background
import com.example.phylax.ui.theme.LocationBlue
import kotlinx.coroutines.flow.distinctUntilChanged
import java.net.URLDecoder
import java.net.URLEncoder
import com.example.phylax.ui.screens.auth.*
import com.example.phylax.ui.screens.emergency.*
import com.example.phylax.ui.screens.home.*
import com.example.phylax.ui.screens.setup.*

@Composable
fun PhylaxNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = viewModel()

    // Wait until Firebase has restored the persisted session before choosing
    // the start screen. Reading currentUser synchronously can return null on a
    // cold start, which used to show Welcome even when the user was signed in.
    if (!authViewModel.uiState.isInitialized) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = LocationBlue)
        }
        return
    }

    val startDestination = remember {
        if (authViewModel.uiState.user != null) Screen.Home.route
        else Screen.Welcome.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authRoutes = remember {
        setOf(
            Screen.Welcome.route,
            Screen.Login.route,
            Screen.Register.route,
            Screen.ForgotPassword.route
        )
    }

    // Send the user back to Welcome only when they sign out.
    LaunchedEffect(authViewModel.uiState.user, authViewModel.uiState.isInitialized, currentRoute) {
        if (!authViewModel.uiState.isInitialized) return@LaunchedEffect
        val user = authViewModel.uiState.user
        if (user == null && currentRoute != null && currentRoute !in authRoutes) {
            navController.navigate(Screen.Welcome.route) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(onGetStarted = { navController.navigate(Screen.Login.route) })
        }

        composable(Screen.Register.route) {
            RegistrationScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.ContactsSetup.route)
                },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                viewModel = authViewModel
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                viewModel = authViewModel
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                viewModel = authViewModel
            )
        }

        composable(Screen.ContactsSetup.route) {
            EmergencyContactsScreen(
                onContinue = { navController.navigate(Screen.Permissions.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Permissions.route) {
            PermissionScreen(
                onAllow = { navController.navigate(Screen.SafetySetup.route) },
                onNotNow = { navController.navigate(Screen.SafetySetup.route) }
            )
        }

        composable(Screen.SafetySetup.route) {
            SafetySettingsScreen(onFinish = { navController.navigate(Screen.Home.route) })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSOS = { navController.navigate(Screen.EmergencyDashboard.route) },
                onNavigateToContacts = { navController.navigate(Screen.ContactsSetup.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAlarm = { navController.navigate(Screen.LoudAlarm.route) },
                onNavigateToLocation = { navController.navigate(Screen.LiveLocation.route) },
                onNavigateToMessages = { navController.navigate(Screen.Messages.route) }
            )
        }

        composable(Screen.LoudAlarm.route) {
            LoudAlarmScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.EmergencyDashboard.route) {
            val userId = authViewModel.uiState.user?.uid
            if (userId != null) {
                EmergencyDashboard(
                    userId = userId,
                    onStopSOS = { navController.navigate(Screen.Home.route) },
                    onViewMap = { navController.navigate(Screen.LiveLocation.route) }
                )
            } else {
                LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) }
            }
        }

        composable(Screen.LiveLocation.route) {
            LiveLocationScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Messages.route) {
            MessagesScreen(
                navController = navController,
                onNavigateToChat = { userId, userName ->
                    val encodedUserName = URLEncoder.encode(userName, "UTF-8")
                    navController.navigate("chat/$userId/$encodedUserName")
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onRecordClick = { session ->
                    navController.navigate(Screen.HistoryDetail.createRoute(session.id.orEmpty()))
                }
            )
        }

        composable(
            route = Screen.HistoryDetail.route,
            arguments = listOf(navArgument("sessionId") { defaultValue = "" })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            HistoryDetailScreen(
                sessionId = sessionId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.SafetyTips.route) {
            SafetyTipsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.onEvent(AuthUserEvent.Logout)
                },
                onNavigateToContacts = { navController.navigate(Screen.ContactsSetup.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) },
                onNavigateToSafetySetup = { navController.navigate(Screen.SafetySetup.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                viewModel = authViewModel
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("userId") { defaultValue = "" },
                navArgument("userName") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val encodedUserName = backStackEntry.arguments?.getString("userName") ?: ""
            val userName = java.net.URLDecoder.decode(encodedUserName, "UTF-8")
            ChatScreen(
                otherUserId = userId,
                otherUserName = userName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}