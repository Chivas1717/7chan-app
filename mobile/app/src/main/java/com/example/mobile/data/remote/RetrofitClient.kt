package com.example.mobile.data.remote

import com.example.mobile.util.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "https://sevenchan-app.onrender.com/api/"

    fun create(tokenManager: TokenManager): ApiService {
        // Інтерцептор для додавання токена
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            // Отримуємо токен із TokenManager
            val token = tokenManager.getToken()
            if (!token.isNullOrEmpty()) {
                // Додаємо токен у заголовок Authorization
                requestBuilder.addHeader("Authorization", "Token $token")
            }

            chain.proceed(requestBuilder.build())
        }

        // OkHttpClient з інтерцептором
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        // Створення Retrofit
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }
}
