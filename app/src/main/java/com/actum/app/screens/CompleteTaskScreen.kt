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
import com.actum.app.network.CompleteTaskRequest
import com.actum.app.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun CompleteTaskScreen(
    task: TaskItem,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit
) {
    var workDone by remember { mutableStateOf("") }
    var client by remember { mutableStateOf(task.clientName) }
    var result by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Завершение заявки")

        Spacer(modifier = Modifier.height(16.dp))

        Text("ID: ${task.id}")
        Text("Название: ${task.title}")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = workDone,
            onValueChange = { workDone = it },
            label = { Text("Что сделано") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = client,
            onValueChange = { client = it },
            label = { Text("Клиент") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = result,
            onValueChange = { result = it },
            label = { Text("Результат") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val json = """
                            {
                              "workDone":"$workDone",
                              "client":"$client",
                              "result":"$result"
                            }
                        """.trimIndent()

                        RetrofitClient.taskActionApi.completeTask(
                            CompleteTaskRequest(
                                taskId = task.id,
                                data = json
                            )
                        )

                        onCompleted()
                    } catch (e: Exception) {
                        message = "Ошибка: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Завершить заявку")
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