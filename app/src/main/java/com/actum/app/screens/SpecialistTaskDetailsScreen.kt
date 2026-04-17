package com.actum.app.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.actum.app.model.TaskItem
import com.actum.app.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialistTaskDetailsScreen(
    task: TaskItem,
    specialistId: Long,
    onBackClick: () -> Unit,
    onActionDone: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var cancelReason by remember { mutableStateOf("") }
    var cancelReasonError by remember { mutableStateOf("") }
    var showCompleteForm by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (showCompleteForm) {
        CompleteTaskScreen(
            task = task,
            onBackClick = { showCompleteForm = false },
            onCompleted = { onActionDone() }
        )
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Карточка заявки")
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
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PriorityBadge(task.priority)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Информация о заявке", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Заявка #: ${task.id}")
                    Text("Адрес: ${task.address}")
                    Text("Статус: ${task.status.toRussianStatus()}")
                    Text("Срок выполнения: ${task.deadline ?: "-"}")

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Информация о клиенте", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Имя клиента: ${task.clientName}")
                    Text("Телефон клиента: ${task.clientPhone ?: "-"}")

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Исполнитель", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Специалист: ${
                            task.specialistFullName ?: "Нет специалиста"
                        }"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Действия",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!task.clientPhone.isNullOrBlank()) {
                        Button(
                            onClick = {
                                openDialer(context, task.clientPhone)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Позвонить клиенту")
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (task.status == "CREATED") {
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        RetrofitClient.takeTaskApi.takeTask(
                                            taskId = task.id,
                                            specialistId = specialistId
                                        )
                                        onActionDone()
                                    } catch (e: Exception) {
                                        message = "Ошибка: ${e.message}"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = specialistId > 0
                        ) {
                            Text("Взять в работу")
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (task.status == "IN_PROGRESS") {
                        Button(
                            onClick = {
                                showCompleteForm = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Перейти к завершению")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = cancelReason,
                            onValueChange = {
                                if (it.length <= 200) {
                                    cancelReason = it
                                    cancelReasonError = ""
                                    message = ""
                                }
                            },
                            label = { Text("Причина отмены") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = cancelReasonError.isNotEmpty(),
                            supportingText = {
                                if (cancelReasonError.isNotEmpty()) {
                                    Text(cancelReasonError)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val reason = cancelReason.trim()

                                if (reason.isEmpty()) {
                                    cancelReasonError = "Введите причину отмены"
                                    return@Button
                                }

                                if (reason.length < 5) {
                                    cancelReasonError = "Причина должна быть не менее 5 символов"
                                    return@Button
                                }

                                scope.launch {
                                    try {
                                        RetrofitClient.taskActionApi.cancelTask(
                                            taskId = task.id,
                                            reason = reason
                                        )
                                        onActionDone()
                                    } catch (e: Exception) {
                                        message = "Ошибка: ${e.message}"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Отменить заявку")
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(message)
            }
        }
    }
}

private fun openDialer(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:$phone".toUri()
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

private fun String.toRussianStatus(): String {
    return when (this) {
        "CREATED" -> "Создана"
        "IN_PROGRESS" -> "В работе"
        "DONE" -> "Выполнена"
        "CANCELLED" -> "Отменена"
        else -> this
    }
}