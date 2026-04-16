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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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

@Composable
fun ManagerTaskDetailsScreen(
    task: TaskItem,
    onBackClick: () -> Unit
) {
    var reportText by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var savedPdfFile by remember { mutableStateOf<File?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Карточка заявки",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

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

                Text("Заявка", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("ID: ${task.id}")
                Text("Адрес: ${task.address}")
                Text("Статус: ${task.status}")
                Text("Срок: ${task.deadline ?: "-"}")

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text("Клиент", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Имя: ${task.clientName}")
                Text("Телефон: ${task.clientPhone ?: "-"}")

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text("Исполнители", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Менеджер ID: ${task.managerId}")
                Text("Специалист ID: ${task.specialistId ?: "-"}")
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
                                    val report = RetrofitClient.reportApi.getReport(task.id)
                                    reportText = report.data
                                    message = ""
                                } catch (e: Exception) {
                                    message = "Ошибка: ${e.message}"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Открыть отчёт")
                    }

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

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Назад")
                    }
                }
            }
        } else {
            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(message)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (reportText.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Отчёт",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reportText,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                }
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