package com.example.mobile.data.remote

import com.example.mobile.viewmodel.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

    @GET("users/{id}/")
    suspend fun getUserProfile(@Path("id") id: Int): UserProfileResponse

    @DELETE("posts/{id}/")
    suspend fun deletePost(@Path("id") postId: Int)

}
