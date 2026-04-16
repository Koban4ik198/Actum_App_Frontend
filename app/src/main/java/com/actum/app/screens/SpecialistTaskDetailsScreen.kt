package com.actum.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.actum.app.model.TaskItem
import com.actum.app.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun SpecialistTaskDetailsScreen(
    task: TaskItem,
    onBackClick: () -> Unit,
    onActionDone: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var cancelReason by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Карточка заявки")

        Spacer(modifier = Modifier.height(16.dp))

        Text("ID: ${task.id}")
        Text("Название: ${task.title}")
        Text("Адрес: ${task.address}")
        Text("Клиент: ${task.clientName}")
        Text("Статус: ${task.status}")

        Spacer(modifier = Modifier.height(24.dp))

        if (task.status == "CREATED") {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            RetrofitClient.takeTaskApi.takeTask(
                                taskId = task.id,
                                specialistId = 2
                            )
                            onActionDone()
                        } catch (e: Exception) {
                            message = "Ошибка: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Взять в работу")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (task.status == "IN_PROGRESS") {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            RetrofitClient.taskActionApi.completeTask(task.id)
                            onActionDone()
                        } catch (e: Exception) {
                            message = "Ошибка: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Завершить")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cancelReason,
                onValueChange = { cancelReason = it },
                label = { Text("Причина отмены") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            RetrofitClient.taskActionApi.cancelTask(
                                taskId = task.id,
                                reason = cancelReason
                            )
                            onActionDone()
                        } catch (e: Exception) {
                            message = "Ошибка: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Отменить")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

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