package com.example.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobile.viewmodel.ProfileViewModel
import com.example.mobile.viewmodel.UserProfileResponse
import com.example.mobile.viewmodel.PostResponse

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    userId: Int,
    onNavigateToPostDetails: (Int) -> Unit,
    onLogout: () -> Unit,
    onNavigateToCreatePost: () -> Unit // <-- Додали цей колбек
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                backgroundColor = Color(0xFF202433),
                actions = {
                    // Кнопка логауту
                    TextButton(
                        onClick = {
                            viewModel.logout()
                            onLogout()
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Logout", color = Color.Red)
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    errorMessage != null -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = errorMessage ?: "Unknown error",
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                            )
                            Button(
                                onClick = { viewModel.fetchUserProfile(userId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF202433))
                            ) {
                                Text("Refresh", color = Color.White)
                            }
                        }
                    }
                    userProfile != null -> {
                        UserProfileContent(
                            profile = userProfile!!,
                            onDeletePost = { viewModel.deletePost(it) },
                            onPostClick = onNavigateToPostDetails,
                            onRefresh = { viewModel.fetchUserProfile(userId) },
                            onNavigateToCreatePost = onNavigateToCreatePost // <-- Передаємо далі
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun UserProfileContent(
    profile: UserProfileResponse,
    onDeletePost: (Int) -> Unit,
    onPostClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToCreatePost: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E2C))
            .padding(top = 16.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // 1) Шапка профілю
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E2C))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Ось тут додаємо аватар
                        UserProfileAvatar()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Далі — email та username
                        Text(profile.email, style = MaterialTheme.typography.h5, color = Color.White)
                        Text("@" + profile.username, style = MaterialTheme.typography.body2, color = Color.Gray)
                    }
                }
            }

            // 2) Кнопка для оновлення профілю (рефреш)
            if (profile.posts.isNotEmpty()) {
                item {
                    Button(
                        onClick = onRefresh,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF202433))
                    ) {
                        Text("Refresh", color = Color.White)
                    }
                }
            }

            // 3) Якщо постів немає — показуємо повідомлення і кнопку "Create Post"
            if (profile.posts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "У вас немає жодного поста",
                            color = Color.White,
                            style = MaterialTheme.typography.subtitle1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onNavigateToCreatePost,
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF202433))
                        ) {
                            Text("Створити пост", color = Color.White)
                        }
                    }
                }
            } else {
                // 4) Якщо пости є, відображаємо їх
                items(profile.posts) { post ->
                    PostItem(
                        post = post,
                        onClick = { onPostClick(post.id) },
                        onDelete = { onDeletePost(post.id) }
                    )
                }
            }
        }
    }
}


@Composable
fun PostItem(
    post: PostResponse,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = 4.dp,
        backgroundColor = Color(0xFF202433)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(post.title, style = MaterialTheme.typography.h6, color = Color.White)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.content, style = MaterialTheme.typography.body2, color = Color.LightGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Автор: ${post.author.username}",
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(post.hashtag_list ?: emptyList()) { hashtag ->
                    Text(
                        text = "#$hashtag",
                        style = MaterialTheme.typography.body2,
                        color = Color.Cyan,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(32.dp),
                    backgroundColor = Color(0xFF2F3241),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = "Response",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add response",
                            color = Color.White,
                            style = MaterialTheme.typography.button
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${post.comments.size}",
                            color = Color(0xFFFF4B6E),
                            style = MaterialTheme.typography.button
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun UserProfileAvatar() {
    Box(
        modifier = Modifier
            .size(80.dp)               // розмір “аватарки”
            .clip(CircleShape)         // заокруглюємо форму в коло
            .background(Color.Gray),   // базовий фон (для фейкового аватара)
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User avatar",
            tint = Color.White,
            modifier = Modifier.size(48.dp) // розмір самої іконки
        )
    }
}

