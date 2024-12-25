package com.example.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile.ui.screens.*
import com.example.mobile.viewmodel.ProfileViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object PostDetails : Screen("post_details/{postId}") {
        fun createRoute(postId: Int) = "post_details/$postId"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    profileViewModel: ProfileViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // Екран логіна
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    // Зберегти token і перейти на Home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Екран реєстрації
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // Зберегти token і перейти на Home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Екран Home (головна стрічка)
        composable(Screen.Home.route) {
            HomeScreen(
//                onNavigateToProfile = {
//                    navController.navigate(Screen.Profile.route)
//                }
            )
        }

        // Екран профілю
        composable(Screen.Profile.route + "/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            if (userId != null) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    userId = userId, // Передаємо id до ProfileScreen
                    onNavigateToPostDetails = { postId ->
                        navController.navigate(Screen.PostDetails.createRoute(postId))
                    }
                )
            }
        }

        // Екран деталей поста
//        composable(Screen.PostDetails.route) { backStackEntry ->
//            val postId = backStackEntry.arguments?.getString("postId")?.toIntOrNull()
//            if (postId != null) {
//                PostDetailsScreen(postId = postId, onNavigateBack = {
//                    navController.popBackStack()
//                })
//            }
//        }
    }
}
