package com.example.mobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mobile.data.remote.LoginRequest
import com.example.mobile.data.remote.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    // Стан для Username та Password
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.h5
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Unspecified),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colors.error)
            }
            // Кнопка логіну (поки без логіки)
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        print("here");
                        try {
                            // У Django логін вимагає username чи email?
                            // Припустімо, у бекенді "username" — це email
                            val response = RetrofitClient.apiService.login(
                                LoginRequest(username = username, password = password)
                            )
                            // Якщо OK, зберігаємо token
                            onLoginSuccess(response.token)
                        } catch (e: Exception) {
                            errorMessage = e.localizedMessage ?: "Login failed"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isLoading) "Loading..." else "Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Посилання "Don't have an account? Register"
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Don’t have an account? ")
                Text(
                    text = "Create one!",
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .clickable {
                            onNavigateToRegister()
                        }
                )
            }
        }
    }
}
