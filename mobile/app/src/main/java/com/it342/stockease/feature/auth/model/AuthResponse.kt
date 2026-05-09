package com.it342.stockease.feature.auth.model

data class AuthResponse(
    val success: Boolean,
    val data: AuthData?,
    val error: ErrorData?,
    val timestamp: Any?
)

data class AuthData(
    val user: UserData?,
    val accessToken: String?,
    val refreshToken: String?
)

data class UserData(
    val email: String,
    val firstname: String,
    val lastname: String,
    val role: String
)

data class ErrorData(
    val code: String?,
    val message: String?
)
