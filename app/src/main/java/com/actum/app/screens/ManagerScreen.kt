package com.actum.app.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.actum.app.model.TaskItem
import com.actum.app.network.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerScreen(
    managerFullName: String,
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    var tasks by remember { mutableStateOf<List<TaskItem>>(emptyList()) }
    var message by remember { mutableStateOf("Загрузка...") }
    var selectedTab by remember { mutableStateOf("Невыполненные") }
    var openedTask by remember { mutableStateOf<TaskItem?>(null) }

    LaunchedEffect(Unit) {
        try {
            tasks = RetrofitClient.taskApi.getTasks()
            message = ""
        } catch (e: Exception) {
            message = "Ошибка загрузки: ${e.message}"
        }
    }

    if (openedTask != null) {
        ManagerTaskDetailsScreen(
            task = openedTask!!,
            onBackClick = { openedTask = null }
        )
    } else {
        val filteredTasks = tasks.filter { task ->
            when (selectedTab) {
                "Невыполненные" -> task.status == "CREATED"
                "В процессе" -> task.status == "IN_PROGRESS"
                "Выполненные" -> task.status == "DONE"
                "Отменённые" -> task.status == "CANCELLED"
                else -> true
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Заявки менеджера")
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onCreateClick
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Создать заявку"
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (managerFullName.isNotBlank()) {
                    Text(
                        text = "Менеджер: $managerFullName",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ManagerTabChip(
                        text = "Невыполненные",
                        selected = selectedTab == "Невыполненные",
                        onClick = { selectedTab = "Невыполненные" }
                    )
                    ManagerTabChip(
                        text = "В процессе",
                        selected = selectedTab == "В процессе",
                        onClick = { selectedTab = "В процессе" }
                    )
                    ManagerTabChip(
                        text = "Выполненные",
                        selected = selectedTab == "Выполненные",
                        onClick = { selectedTab = "Выполненные" }
                    )
                    ManagerTabChip(
                        text = "Отменённые",
                        selected = selectedTab == "Отменённые",
                        onClick = { selectedTab = "Отменённые" }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (message.isNotEmpty()) {
                    Text(message)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredTasks) { task ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Номер заявки: ${task.id}")
                                Text("Адрес: ${task.address}")
                                Text("Клиент: ${task.clientName}")
                                Text("Телефон клиента: ${task.clientPhone ?: "-"}")
                                Text("Срок выполнения: ${task.deadline ?: "-"}")
                                Text("Статус: ${task.status.toRussianStatus()}")
                                Text(
                                    "Специалист: ${
                                        task.specialistFullName?.ifBlank { "Нет специалиста" }
                                            ?: "Нет специалиста"
                                    }"
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                PriorityBadge(task.priority)

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { openedTask = task }
                                ) {
                                    Text("Открыть")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManagerTabChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) }
    )
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