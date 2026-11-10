package com.example.phylax.data.model

data class EmergencySession(
    val id: String? = null,
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val status: String = "ACTIVE", // ACTIVE, ENDED
    val note: String = ""
)

data class LocationUpdate(
    val id: String? = null,
    val sessionId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
