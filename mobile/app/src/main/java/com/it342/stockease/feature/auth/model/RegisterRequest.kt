package com.it342.stockease.feature.auth.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val role: String = "STAFF"
)
