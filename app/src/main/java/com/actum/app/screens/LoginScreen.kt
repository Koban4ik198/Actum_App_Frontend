package com.actum.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.actum.app.network.LoginRequest
import com.actum.app.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    role: String,
    onBackClick: () -> Unit,
    onLoginSuccess: (userId: Long, fullName: String) -> Unit
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    var loginError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Actum",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Вход: $role",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                OutlinedTextField(
                    value = login,
                    onValueChange = {
                        login = it
                        loginError = ""
                        message = ""
                    },
                    label = { Text("Логин") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    isError = loginError.isNotEmpty(),
                    supportingText = {
                        if (loginError.isNotEmpty()) {
                            Text(loginError)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = ""
                        message = ""
                    },
                    label = { Text("Пароль") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    isError = passwordError.isNotEmpty(),
                    supportingText = {
                        if (passwordError.isNotEmpty()) {
                            Text(passwordError)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        loginError = ""
                        passwordError = ""
                        message = ""

                        var valid = true

                        if (login.trim().isEmpty()) {
                            loginError = "Введите логин"
                            valid = false
                        }

                        if (password.trim().isEmpty()) {
                            passwordError = "Введите пароль"
                            valid = false
                        } else if (password.trim().length < 4) {
                            passwordError = "Минимум 4 символа"
                            valid = false
                        }

                        if (!valid) return@Button

                        scope.launch {
                            isLoading = true
                            try {
                                val response = RetrofitClient.authApi.login(
                                    LoginRequest(
                                        login = login.trim(),
                                        password = password.trim()
                                    )
                                )

                                val expectedRole = when (role) {
                                    "Специалист" -> "SPECIALIST"
                                    "Менеджер" -> "MANAGER"
                                    else -> ""
                                }

                                if (response.role != expectedRole) {
                                    message = "Эта учётная запись не подходит для роли \"$role\""
                                    return@launch
                                }

                                if (response.token.isNotEmpty()) {
                                    onLoginSuccess(response.userId, response.fullName)
                                } else {
                                    message = "Ошибка входа"
                                }
                            } catch (e: Exception) {
                                message = "Ошибка входа"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Вход..." else "Войти")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    enabled = !isLoading
                ) {
                    Text("Назад")
                }

                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}