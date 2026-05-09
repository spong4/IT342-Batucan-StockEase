package com.it342.stockease.shared.network

import com.it342.stockease.feature.auth.model.AuthResponse
import com.it342.stockease.feature.auth.model.LoginRequest
import com.it342.stockease.feature.auth.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
