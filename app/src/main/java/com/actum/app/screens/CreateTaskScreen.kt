package com.actum.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.actum.app.network.CreateTaskRequest
import com.actum.app.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun CreateTaskScreen(
    onBackClick: () -> Unit,
    onTaskCreated: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("NORMAL") }
    var deadline by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Создание заявки",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Адрес") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = clientName,
            onValueChange = { clientName = it },
            label = { Text("Клиент") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = clientPhone,
            onValueChange = { clientPhone = it },
            label = { Text("Телефон клиента") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Срочность")

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            PriorityButton(
                text = "NORMAL",
                current = priority,
                onClick = { priority = "NORMAL" }
            )

            PriorityButton(
                text = "URGENT",
                current = priority,
                onClick = { priority = "URGENT" }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = deadline,
            onValueChange = { deadline = it },
            label = { Text("Срок выполнения") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        RetrofitClient.taskApi.createTask(
                            CreateTaskRequest(
                                title = title,
                                address = address,
                                clientName = clientName,
                                clientPhone = clientPhone,
                                priority = priority,
                                deadline = deadline,
                                managerId = 1
                            )
                        )
                        onTaskCreated()
                    } catch (e: Exception) {
                        message = "Ошибка: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Создать")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(message)
        }
    }
}

@Composable
fun PriorityButton(
    text: String,
    current: String,
    onClick: () -> Unit
) {
    val selected = current == text

    Button(
        onClick = onClick,
        colors = if (selected) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        },
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(text)
    }
}