package com.example.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mobile.data.remote.RetrofitClient
import com.example.mobile.ui.navigation.AppNavGraph
import com.example.mobile.ui.theme.MobileTheme
import com.example.mobile.util.TokenManager
import com.example.mobile.viewmodel.ProfileViewModel
import com.example.mobile.viewmodel.ProfileViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(applicationContext)
        val apiService = RetrofitClient.apiService // Ваш API-сервіс

        setContent {
            MobileTheme {
                val token = tokenManager.getToken()
                val userId = tokenManager.getUserId()
                val startDest = if (userId == null) "login" else "profile/$userId"

                val navController = rememberNavController()

                // Ініціалізація ProfileViewModel з фабрикою
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModelFactory(apiService)
                )

                AppNavGraph(
                    navController = navController,
                    startDestination = startDest,
                    profileViewModel = profileViewModel,
                )
            }
        }
    }
}

