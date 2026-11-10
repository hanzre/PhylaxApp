package com.example.phylax.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
