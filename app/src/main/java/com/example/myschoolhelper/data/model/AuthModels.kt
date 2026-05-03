package com.example.myschoolhelper.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val _id: String,
    val name: String,
    val email: String,
    val role: String,
    val token: String,
    val school_id: String? = null
)
