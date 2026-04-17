package com.actum.app.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
fun SpecialistTasksScreen(
    specialistId: Long,
    specialistFullName: String,
    onBackClick: () -> Unit
) {
    var tasks by remember { mutableStateOf<List<TaskItem>>(emptyList()) }
    var message by remember { mutableStateOf("Загрузка...") }
    var selectedTab by remember { mutableStateOf("Все") }
    var openedTask by remember { mutableStateOf<TaskItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Все") }

    var statusExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("Все", "Доступные", "В процессе", "Выполненные", "Отменённые")
    val priorityOptions = listOf("Все", "Низкий", "Обычный", "Срочный")

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
            specialistId = specialistId,
            onBackClick = { openedTask = null },
            onActionDone = {
                openedTask = null
                message = "Обновление..."
            }
        )
        return
    }

    val statusFilteredTasks = tasks.filter { task ->
        when (selectedTab) {
            "Все" -> true
            "Доступные" -> task.status == "CREATED"
            "В процессе" -> task.status == "IN_PROGRESS"
            "Выполненные" -> task.status == "DONE"
            "Отменённые" -> task.status == "CANCELLED"
            else -> true
        }
    }

    val priorityFilteredTasks = statusFilteredTasks.filter { task ->
        when (selectedPriority) {
            "Низкий" -> task.priority == "LOW"
            "Обычный" -> task.priority == "NORMAL"
            "Срочный" -> task.priority == "URGENT"
            else -> true
        }
    }

    val query = searchQuery.trim().lowercase()

    val filteredTasks = priorityFilteredTasks.filter { task ->
        if (query.isBlank()) {
            true
        } else {
            listOf(
                task.id.toString(),
                task.title,
                task.address,
                task.clientName,
                task.clientPhone ?: "",
                task.deadline ?: "",
                task.specialistFullName ?: "",
                task.status.toRussianStatusSpecialist(),
                task.priority.toRussianPrioritySpecialist()
            ).any { value ->
                value.lowercase().contains(query)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Заявки специалиста")
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            if (specialistFullName.isNotBlank()) {
                Text(
                    text = "Специалист: $specialistFullName",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Поиск") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Фильтры",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { statusExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Статус: $selectedTab", modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Выбрать статус"
                        )
                    }

                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedTab = option
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { priorityExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Приоритет: $selectedPriority", modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Выбрать приоритет"
                        )
                    }

                    DropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        priorityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedPriority = option
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (message.isNotEmpty()) {
                Text(message)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredTasks.isEmpty() && message.isEmpty()) {
                Text("По вашему запросу ничего не найдено")
                Spacer(modifier = Modifier.height(12.dp))
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
                                text = "Заявка #: ${task.id}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Услуга: ${task.title}")
                            Text("Адрес: ${task.address}")
                            Text("Срок выполнения: ${task.deadline ?: "-"}")
                            Text("Статус: ${task.status.toRussianStatusSpecialist()}")
                            Text("Специалист: ${task.specialistFullName ?: "Нет специалиста"}")

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

private fun String.toRussianStatusSpecialist(): String {
    return when (this) {
        "CREATED" -> "Создана"
        "IN_PROGRESS" -> "В работе"
        "DONE" -> "Выполнена"
        "CANCELLED" -> "Отменена"
        else -> this
    }
}

private fun String?.toRussianPrioritySpecialist(): String {
    return when (this) {
        "LOW" -> "Низкий"
        "NORMAL" -> "Обычный"
        "URGENT" -> "Срочный"
        else -> ""
    }
}