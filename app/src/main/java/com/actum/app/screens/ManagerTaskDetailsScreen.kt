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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.actum.app.model.TaskItem
import com.actum.app.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerTaskDetailsScreen(
    task: TaskItem,
    onBackClick: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var savedPdfFile by remember { mutableStateOf<File?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                    Text("Номер заявки: ${task.id}")
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

                    Text("Исполнители", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Менеджер: ${task.managerFullName ?: "-"}")
                    Text("Специалист: ${task.specialistFullName ?: "Нет специалиста"}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (task.status == "DONE" || task.status == "CANCELLED") {
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

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val body = RetrofitClient.pdfApi.downloadPdf(task.id)
                                        val file = savePdfToFile(context, task.id, body.bytes())
                                        savedPdfFile = file
                                        message = "PDF сохранён: ${file.absolutePath}"
                                    } catch (e: Exception) {
                                        message = "Ошибка PDF: ${e.message}"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Скачать PDF")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (savedPdfFile != null) {
                            Button(
                                onClick = {
                                    openPdf(context, savedPdfFile!!)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Открыть PDF")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(message)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

private fun savePdfToFile(
    context: Context,
    taskId: Long,
    bytes: ByteArray
): File {
    val file = File(context.filesDir, "task-report-$taskId.pdf")
    file.writeBytes(bytes)
    return file
}

private fun openPdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "com.actum.app.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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