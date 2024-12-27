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
import com.example.mobile.viewmodel.HashtagResponse
import com.example.mobile.viewmodel.HomeViewModel
import com.example.mobile.viewmodel.PostResponse

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToPostDetails: (Int) -> Unit
) {
    val hashtags by viewModel.hashtags.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedHashtag by viewModel.selectedHashtag.collectAsState()

    // При першому запуску:
    LaunchedEffect(Unit) {
        // 1. Отримуємо всі хештеги
        viewModel.fetchHashtags()
        // 2. Отримуємо всі пости
        viewModel.fetchPosts(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home", color = Color.White) },
                backgroundColor = Color(0xFF202433)
            )
        },
        content = { padding ->
            // Використаємо Column чи Box, аби розташувати чіпи + секцію постів
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF1E1E2C))
            ) {
                Column {
                    // 1) Горизонтальний ряд хештегів завжди показуємо
                    HashtagChips(
                        hashtags = hashtags,
                        selectedHashtag = selectedHashtag,
                        onHashtagClick = { clickedHashtag ->
                            viewModel.fetchPosts(clickedHashtag)
                        }
                    )

                    // 2) Секція постів (або помилка / лоадер)
                    when {
                        isLoading -> {
                            // Показуємо локальний лоадер замість списку
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp), // Щоб лоадер не був засунутий
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        errorMessage != null -> {
                            // Показуємо помилку тільки в секції постів
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = errorMessage ?: "Unknown error",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = {
                                        viewModel.fetchPosts(selectedHashtag)
                                    },
                                    modifier = Modifier.padding(top = 16.dp),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF202433))
                                ) {
                                    Text("Retry", color = Color.White)
                                }
                            }
                        }
                        else -> {
                            // Показуємо список постів
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


@Composable
fun HashtagChips(
    hashtags: List<HashtagResponse>,
    selectedHashtag: String?,
    onHashtagClick: (String?) -> Unit
) {
    // Додаємо "All" (без фільтра)
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Чіп "All"
        item {
            FilterChip(
                text = "All",
                isSelected = selectedHashtag == null,
                onClick = { onHashtagClick(null) }
            )
        }

        // Чіпи з бека
        items(hashtags) { hashtagItem ->
            val name = hashtagItem.name
            FilterChip(
                text = name,
                isSelected = name == selectedHashtag,
                onClick = { onHashtagClick(name) }
            )
        }
    }
}

// Простий чіп
@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Color.Magenta else Color(0xFF2F3241),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Text(
            text = "#$text",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White
        )
    }
}

