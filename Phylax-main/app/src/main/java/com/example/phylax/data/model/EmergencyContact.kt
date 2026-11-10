package com.example.phylax.data.model

data class EmergencyContact(
    val id: String? = null,
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val isPrimary: Boolean = false
)
