package com.it342.stockease.network.models

data class LoginRequest(
    val email: String,
    val password: String
)