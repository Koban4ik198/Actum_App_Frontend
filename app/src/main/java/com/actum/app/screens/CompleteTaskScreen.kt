package com.actum.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.actum.app.model.TaskItem
import com.actum.app.network.CompleteTaskRequest
import com.actum.app.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteTaskScreen(
    task: TaskItem,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit
) {
    var workDone by remember { mutableStateOf("") }
    var client by remember { mutableStateOf(task.clientName) }
    var result by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    var workDoneError by remember { mutableStateOf("") }
    var clientError by remember { mutableStateOf("") }
    var resultError by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Завершение заявки")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text("Заявка #: ${task.id}")
            Text("Название: ${task.title}")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = workDone,
                onValueChange = {
                    workDone = it
                    workDoneError = ""
                    message = ""
                },
                label = { Text("Что выполнено") },
                modifier = Modifier.fillMaxWidth(),
                isError = workDoneError.isNotEmpty(),
                supportingText = {
                    if (workDoneError.isNotEmpty()) {
                        Text(workDoneError)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = client,
                onValueChange = {
                    client = it
                    clientError = ""
                    message = ""
                },
                label = { Text("Клиент") },
                modifier = Modifier.fillMaxWidth(),
                isError = clientError.isNotEmpty(),
                supportingText = {
                    if (clientError.isNotEmpty()) {
                        Text(clientError)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = result,
                onValueChange = {
                    result = it
                    resultError = ""
                    message = ""
                },
                label = { Text("Результат работы") },
                modifier = Modifier.fillMaxWidth(),
                isError = resultError.isNotEmpty(),
                supportingText = {
                    if (resultError.isNotEmpty()) {
                        Text(resultError)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = price,
                onValueChange = {
                    if (it.all { ch -> ch.isDigit() }) {
                        price = it
                        priceError = ""
                        message = ""
                    }
                },
                label = { Text("Цена (руб)") },
                modifier = Modifier.fillMaxWidth(),
                isError = priceError.isNotEmpty(),
                supportingText = {
                    if (priceError.isNotEmpty()) {
                        Text(priceError)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    message = ""
                    workDoneError = ""
                    clientError = ""
                    resultError = ""
                    priceError = ""

                    var valid = true

                    if (workDone.trim().isEmpty()) {
                        workDoneError = "Введите выполненные работы"
                        valid = false
                    } else if (workDone.trim().length < 5) {
                        workDoneError = "Минимум 5 символов"
                        valid = false
                    }

                    if (client.trim().isEmpty()) {
                        clientError = "Введите имя клиента"
                        valid = false
                    } else if (client.trim().length < 2) {
                        clientError = "Минимум 2 символа"
                        valid = false
                    }

                    if (result.trim().isEmpty()) {
                        resultError = "Введите результат работы"
                        valid = false
                    } else if (result.trim().length < 3) {
                        resultError = "Минимум 3 символа"
                        valid = false
                    }

                    if (price.isBlank()) {
                        priceError = "Введите цену"
                        valid = false
                    } else if (price.toIntOrNull() == null || price.toInt() <= 0) {
                        priceError = "Цена должна быть больше 0"
                        valid = false
                    }

                    if (!valid) return@Button

                    scope.launch {
                        try {
                            val json = """
                                {
                                  "workDone":"${workDone.trim()}",
                                  "client":"${client.trim()}",
                                  "result":"${result.trim()}",
                                  "price":"$price"
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
                Text("Подтвердить завершение")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(message)
            }
        }
    }
}