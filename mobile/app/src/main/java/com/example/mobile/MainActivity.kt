package com.example.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.mobile.ui.navigation.AppNavGraph
import com.example.mobile.ui.theme.MobileTheme
import com.example.mobile.util.getToken

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileTheme {
                val token = getToken(applicationContext)
                val startDest = if (token.isNullOrEmpty()) "login" else "home"

                val navController = rememberNavController()

                AppNavGraph(
                    navController = navController,
                    startDestination = startDest
                )
            }
        }
    }
}
