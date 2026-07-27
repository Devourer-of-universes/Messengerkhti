package com.example.myapplication.model

data class LoginRequest(
    val emailOrUsername: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val surname: String,
    val name: String,
    val patronymic: String? = null,
    val birthday: String,
    val email: String,
    val telNum: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val user: User,
    val token: String
)