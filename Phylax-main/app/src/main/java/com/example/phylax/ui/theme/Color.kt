package com.example.phylax.ui.theme

import androidx.compose.ui.graphics.Color

// iOS 27 Futuristic Palette
val Background = Color(0xFFF2F2F7) 
val Surface = Color(0xFFFFFFFF)
val PrimaryText = Color(0xFF1C1C1E)
val SecondaryText = Color(0xFF8E8E93)

// Glassmorphism Base Colors
val GlassBackground = Color(0xFFFFFFFF).copy(alpha = 0.7f)
val GlassBorder = Color(0xFFFFFFFF).copy(alpha = 0.4f)
val DarkGlassBackground = Color(0xFF1C1C1E).copy(alpha = 0.8f)

// iOS 27 System Accents with Glow support
val iOSBlue = Color(0xFF007AFF)
val iOSBlueGlow = Color(0xFF32ADE6).copy(alpha = 0.3f)
val iOSRed = Color(0xFFFF3B30)
val iOSRedGlow = Color(0xFFFF453A).copy(alpha = 0.3f)
val iOSGreen = Color(0xFF34C759)
val iOSIndigo = Color(0xFF5856D6)
val iOSIndigoGlow = Color(0xFF7D7AFF).copy(alpha = 0.3f)

val LocationBlue = iOSBlue
val EmergencyRed = iOSRed
val SuccessGreen = iOSGreen
val VioletAccent = iOSIndigo
val CyanAccent = Color(0xFF32ADE6)
val BorderColor = Color(0xFFC6C6C8).copy(alpha = 0.3f)

// Mesh Gradient Components (iOS 27 style)
val MeshPink = Color(0xFFFF2D55)
val MeshPurple = Color(0xFFAF52DE)
val MeshBlue = Color(0xFF5AC8FA)

// Gradients
val EmergencyGradientStart = Color(0xFFFF3B30)
val EmergencyGradientEnd = Color(0xFFFF453A)
val VioletGradientStart = Color(0xFF5856D6)
val VioletGradientEnd = Color(0xFF7D7AFF)
val FuturisticGradient = listOf(MeshBlue, MeshPurple, MeshPink)
