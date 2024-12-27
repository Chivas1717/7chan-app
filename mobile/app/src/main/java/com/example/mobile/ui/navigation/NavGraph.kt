package com.example.mobile.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mobile.data.remote.ApiService
import com.example.mobile.ui.screens.*
import com.example.mobile.util.TokenManager
import com.example.mobile.viewmodel.HomeViewModel
import com.example.mobile.viewmodel.NewPostViewModel
import com.example.mobile.viewmodel.PostDetailsViewModel
import com.example.mobile.viewmodel.ProfileViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", Icons.Default.Person)
    object Register : Screen("register", "Refister", Icons.Default.Person)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Profile : Screen("profile/{userId}", "Profile", Icons.Default.Person) {
        fun createRoute(userId: Int) = "profile/$userId"
    }
    object PostDetails : Screen("post/{postId}", "Post Details", Icons.Default.Add) {
        fun createRoute(postId: Int) = "post/$postId"
    }
    object CreatePost : Screen("create_post", "New Thread", Icons.Default.Add)
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    profileViewModel: ProfileViewModel,
    postDetailsViewModel: PostDetailsViewModel,
    homeViewModel: HomeViewModel,
    newPostViewModel: NewPostViewModel,
    apiService: ApiService,
) {
    val items = listOf(
        Screen.Home,
        Screen.CreatePost,
        Screen.Profile
    )
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var currentRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentRoute = backStackEntry.destination.route
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in items.map { it.route }) { // Показуємо тільки на потрібних екранах
                BottomNavigation(
                    backgroundColor = Color(0xFF202433),
                    contentColor = Color.White
                ) {
                    items.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(screen.icon, contentDescription = screen.route) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                when (screen) {
                                    is Screen.Profile -> {
                                        val userId = tokenManager.getUserId()
                                        if (userId != null) {
                                            navController.navigate(Screen.Profile.createRoute(userId))
                                        } else {
                                            navController.navigate(Screen.Login.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }
                                    else -> {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                }
                            },
                            alwaysShowLabel = true,
                            selectedContentColor = Color.Cyan,
                            unselectedContentColor = Color.Gray
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Екран логіна
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        val userId = tokenManager.getUserId()
                        if (userId != null) {
                            navController.navigate(Screen.Profile.createRoute(userId)) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    },
                    apiService = apiService
                )
            }

            // Екран реєстрації
            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        val userId = tokenManager.getUserId()
                        if (userId != null) {
                            navController.navigate(Screen.Profile.createRoute(userId)) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    },
                    apiService = apiService
                )
            }

            // Екран Home (головна стрічка)
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToPostDetails = { postId ->
                        navController.navigate(Screen.PostDetails.createRoute(postId))
                    },
                    viewModel = homeViewModel,
                )
            }

            // Екран профілю
            composable(Screen.Profile.route, arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId")
                if (userId != null) {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        userId = userId,
                        onNavigateToPostDetails = { postId ->
                            navController.navigate(Screen.PostDetails.createRoute(postId))
                        },
                        onLogout = {
                            tokenManager.clearAll()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Profile.route) { inclusive = true }
                            }
                        },
                        onNavigateToCreatePost = {
                            navController.navigate(Screen.CreatePost.route)
                        }
                    )
                }
            }

            composable(Screen.PostDetails.route) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId")?.toIntOrNull()
                if (postId != null) {
                    PostDetailsScreen(
                        viewModel = postDetailsViewModel,
                        postId = postId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.CreatePost.route) {
                NewPostScreen(
                    viewModel = newPostViewModel,
                    onPostSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

        }
    }
}

