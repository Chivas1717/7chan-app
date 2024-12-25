package com.example.mobile.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

// Моделі запитів
data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val email: String, val password: String)

// Моделі відповідей
data class LoginResponse(val token: String, val user_id: Int, val username: String)
data class RegisterResponse(val token: String, val user_id: Int, val username: String, val email: String)

interface ApiService {
    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register/")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}
