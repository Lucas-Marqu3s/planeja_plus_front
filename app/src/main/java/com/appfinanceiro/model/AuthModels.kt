package com.appfinanceiro.model

data class User(
    val id: Long? = null,
    val name: String,
    val email: String
)

data class AuthResponse(
    val accessToken: String,
    val tokenType: String,
    val userId: Long,
    val email: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
