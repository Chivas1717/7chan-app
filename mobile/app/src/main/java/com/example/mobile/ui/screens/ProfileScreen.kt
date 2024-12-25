package com.example.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onNavigateToPostDetails: (Int) -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile(userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Unknown error",
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (userProfile != null) {
            UserProfileContent(
                profile = userProfile!!,
                onDeletePost = { viewModel.deletePost(it) },
                onPostClick = onNavigateToPostDetails
            )
        }
    }
}

@Composable
fun UserProfileContent(
    profile: UserProfileResponse,
    onDeletePost: (Int) -> Unit,
    onPostClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Шапка профілю
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E2C))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(profile.email, style = MaterialTheme.typography.h5, color = Color.White)
                Text("@" + profile.username, style = MaterialTheme.typography.body2, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Список постів
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(profile.posts) { post ->
                PostItem(post = post, onClick = { onPostClick(post.id) }, onDelete = { onDeletePost(post.id) })
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
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(post.title, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.content, style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Created: ${post.createdAt}", style = MaterialTheme.typography.caption)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}
