package com.it342.stockease.network.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val role: String = "STAFF"
)