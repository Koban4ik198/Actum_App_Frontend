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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.actum.app.model.TaskItem
import com.actum.app.network.RetrofitClient

@Composable
fun SpecialistTasksScreen(
    onBackClick: () -> Unit
) {
    var tasks by remember { mutableStateOf<List<TaskItem>>(emptyList()) }
    var message by remember { mutableStateOf("Загрузка...") }
    var selectedTab by remember { mutableStateOf("Доступные") }
    var openedTask by remember { mutableStateOf<TaskItem?>(null) }

    LaunchedEffect(message) {
        try {
            tasks = RetrofitClient.taskApi.getTasks()
            message = ""
        } catch (e: Exception) {
            message = "Ошибка загрузки: ${e.message}"
        }
    }

    if (openedTask != null) {
        SpecialistTaskDetailsScreen(
            task = openedTask!!,
            onBackClick = { openedTask = null },
            onActionDone = {
                openedTask = null
                message = "Обновление..."
            }
        )
        return
    }

    val filteredTasks = tasks.filter { task ->
        when (selectedTab) {
            "Доступные" -> task.status == "CREATED"
            "В процессе" -> task.status == "IN_PROGRESS"
            "Выполненные" -> task.status == "DONE"
            "Отменённые" -> task.status == "CANCELLED"
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Заявки специалиста",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SpecialistTabChip(
                text = "Доступные",
                selected = selectedTab == "Доступные",
                onClick = { selectedTab = "Доступные" }
            )
            SpecialistTabChip(
                text = "В процессе",
                selected = selectedTab == "В процессе",
                onClick = { selectedTab = "В процессе" }
            )
            SpecialistTabChip(
                text = "Выполненные",
                selected = selectedTab == "Выполненные",
                onClick = { selectedTab = "Выполненные" }
            )
            SpecialistTabChip(
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

        if (filteredTasks.isEmpty() && message.isEmpty()) {
            Text("Нет заявок в разделе \"$selectedTab\"")
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

                        Text("Заявка #${task.id}")
                        Text("Адрес: ${task.address}")
                        Text("Клиент: ${task.clientName}")
                        Text("Телефон: ${task.clientPhone ?: "-"}")
                        Text("Срок: ${task.deadline ?: "-"}")
                        Text("Статус: ${task.status}")

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

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выйти")
        }
    }
}

@Composable
fun SpecialistTabChip(
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