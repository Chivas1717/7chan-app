package com.example.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobile.viewmodel.HomeViewModel
import com.example.mobile.viewmodel.PostResponse

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToPostDetails: (Int) -> Unit,
) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPosts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home", color = Color.White) },
                backgroundColor = Color(0xFF202433)
            )
        },
        content = { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else if (errorMessage != null) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Unknown error",
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                        )
                        Button(
                            onClick = { viewModel.fetchPosts() },
                            modifier = Modifier.padding(16.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF202433))
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1E2C))
                            .padding(top = 16.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(posts) { post ->
                                HomePostItem(
                                    post = post,
                                    onClick = { onNavigateToPostDetails(post.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun HomePostItem(
    post: PostResponse,
    onClick: () -> Unit
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
            Text(post.title, style = MaterialTheme.typography.h6, color = Color.White)
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
