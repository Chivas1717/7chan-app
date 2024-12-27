package com.example.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobile.data.remote.ApiService
import com.example.mobile.data.remote.LoginRequest
import com.example.mobile.data.remote.RetrofitClient
import com.example.mobile.util.TokenManager
import com.example.mobile.viewmodel.NewPostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NewPostScreen(
    viewModel: NewPostViewModel,
    onPostSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val hashtag by viewModel.hashtag.collectAsState()
    val content by viewModel.content.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post", color = Color.White) },
                backgroundColor = Color(0xFF202433),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF1E1E2C))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                    }
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.onTitleChange(it) },
                        label = { Text("Title", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color(0xFF202433),
                            textColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = hashtag,
                        onValueChange = { viewModel.onHashtagChange(it) },
                        label = { Text("Hashtag", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color(0xFF202433),
                            textColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { viewModel.onContentChange(it) },
                        label = { Text("Content", color = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color(0xFF202433),
                            textColor = Color.White
                        )
                    )
                    Button(
                        onClick = {
                            viewModel.createPost(onPostSuccess)
                        },
                        enabled = title.isNotBlank() && content.isNotBlank(),
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF202433))
                    ) {
                        Text("Post", color = Color.White)
                    }
                }
            }
        }
    )
}
