package com.example.phylax.ui.navigation

import androidx.navigation.NavType

sealed class Screen(val route: String) {
    // Auth Flow
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    object Register : Screen("register")

    // Setup Flow
    object ContactsSetup : Screen("contacts_setup")
    object Permissions : Screen("permissions")
    object SafetySetup : Screen("safety_setup")

    // Main App
    object Home : Screen("home")
    object SafetyTips : Screen("safety_tips")
    object History : Screen("history")
    object HistoryDetail : Screen("history_detail/{sessionId}") {
        fun createRoute(sessionId: String) = "history_detail/$sessionId"
    }
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object About : Screen("about")
    object LoudAlarm : Screen("loud_alarm")
    object Messages : Screen("messages")
    object Chat : Screen("chat/{userId}/{userName}") {
        fun createRoute(userId: String, userName: String) = "chat/$userId/$userName"
    }

    // Emergency
    object EmergencyDashboard : Screen("emergency_dashboard")
    object LiveLocation : Screen("live_location")
}
