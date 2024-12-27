package com.example.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.material.Scaffold
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobile.viewmodel.PostDetailsResponse
import com.example.mobile.viewmodel.PostDetailsViewModel

@Composable
fun PostDetailsScreen(
    viewModel: PostDetailsViewModel,
    postId: Int,
    onNavigateBack: () -> Unit
) {
    val postDetails by viewModel.postDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val newComment by viewModel.newComment.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPostDetails(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Details", color = Color.White) },
                backgroundColor = Color(0xFF202433),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        content = { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (postDetails != null) {
                    PostDetailsContent(
                        post = postDetails!!,
                        newComment = newComment,
                        onCommentChange = { viewModel.onCommentChange(it) },
                        onPostComment = { viewModel.postComment(postId) }
                    )
                }
            }
        }
    )
}


@Composable
fun PostDetailsContent(
    post: PostDetailsResponse,
    newComment: String,
    onCommentChange: (String) -> Unit,
    onPostComment: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
        .background(Color(0xFF1E1E2C))
        .padding(top = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Деталі посту
            Text(post.title, style = MaterialTheme.typography.h5, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Author: ${post.author.username}", color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.content, style = MaterialTheme.typography.body1, color = Color.LightGray)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(post.hashtag_list) { hashtag ->
                    Text("#$hashtag", style = MaterialTheme.typography.body2, color = Color.Cyan)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Інпут для нового коментаря
            OutlinedTextField(
                value = newComment,
                onValueChange = onCommentChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Write a comment...", color = Color.White) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color(0xFF202433),
                    textColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onPostComment,
                enabled = newComment.isNotBlank(),
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green, disabledBackgroundColor = Color.Gray)
            ) {
                Text("Post Comment", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Список коментарів
            Text("Comments:", style = MaterialTheme.typography.h6, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(post.comments) { comment ->
                    Text(
                        "${comment.author.username}: ${comment.content}",
                        style = MaterialTheme.typography.body2,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
