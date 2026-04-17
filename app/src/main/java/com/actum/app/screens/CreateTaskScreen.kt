package com.actum.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.actum.app.network.CreateTaskRequest
import com.actum.app.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    var titleError by remember { mutableStateOf("") }
    var addressError by remember { mutableStateOf("") }
    var clientNameError by remember { mutableStateOf("") }
    var clientPhoneError by remember { mutableStateOf("") }
    var deadlineError by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val todayStartMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= todayStartMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            deadline = formatter.format(Date(selectedMillis))
                            deadlineError = ""
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Выбрать")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Создание заявки")
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
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = ""
                    message = ""
                },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError.isNotEmpty(),
                supportingText = {
                    if (titleError.isNotEmpty()) {
                        Text(titleError)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                    addressError = ""
                    message = ""
                },
                label = { Text("Адрес") },
                modifier = Modifier.fillMaxWidth(),
                isError = addressError.isNotEmpty(),
                supportingText = {
                    if (addressError.isNotEmpty()) {
                        Text(addressError)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = clientName,
                onValueChange = {
                    clientName = it
                    clientNameError = ""
                    message = ""
                },
                label = { Text("Клиент") },
                modifier = Modifier.fillMaxWidth(),
                isError = clientNameError.isNotEmpty(),
                supportingText = {
                    if (clientNameError.isNotEmpty()) {
                        Text(clientNameError)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = clientPhone,
                onValueChange = {
                    clientPhone = it
                    clientPhoneError = ""
                    message = ""
                },
                label = { Text("Телефон клиента") },
                modifier = Modifier.fillMaxWidth(),
                isError = clientPhoneError.isNotEmpty(),
                supportingText = {
                    if (clientPhoneError.isNotEmpty()) {
                        Text(clientPhoneError)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Срочность")

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                PriorityButton(
                    value = "LOW",
                    label = "Низкий",
                    current = priority,
                    onClick = {
                        priority = "LOW"
                        deadline = ""
                        deadlineError = ""
                        showDatePicker = false
                    }
                )

                PriorityButton(
                    value = "NORMAL",
                    label = "Обычный",
                    current = priority,
                    onClick = { priority = "NORMAL" }
                )

                PriorityButton(
                    value = "URGENT",
                    label = "Срочный",
                    current = priority,
                    onClick = { priority = "URGENT" }
                )
            }

            if (priority != "LOW") {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Срок выполнения") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = deadlineError.isNotEmpty(),
                        supportingText = {
                            if (deadlineError.isNotEmpty()) {
                                Text(deadlineError)
                            }
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Выбрать дату"
                            )
                        }
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                showDatePicker = true
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    message = ""
                    titleError = ""
                    addressError = ""
                    clientNameError = ""
                    clientPhoneError = ""
                    deadlineError = ""

                    var valid = true

                    if (title.trim().isEmpty()) {
                        titleError = "Введите название"
                        valid = false
                    }

                    if (address.trim().isEmpty()) {
                        addressError = "Введите адрес"
                        valid = false
                    }

                    if (clientName.trim().isEmpty()) {
                        clientNameError = "Введите имя клиента"
                        valid = false
                    }

                    val cleanedPhone = clientPhone.filter { it.isDigit() }
                    val phonePattern = Regex("^[+0-9()\\-\\s]+$")

                    if (clientPhone.trim().isEmpty()) {
                        clientPhoneError = "Введите номер телефона"
                        valid = false
                    } else if (!phonePattern.matches(clientPhone.trim())) {
                        clientPhoneError = "Допустимы только цифры, +, пробел, скобки и дефис"
                        valid = false
                    } else if (cleanedPhone.length < 11) {
                        clientPhoneError = "Номер телефона должен содержать минимум 11 цифр"
                        valid = false
                    }

                    if (priority != "LOW" && deadline.trim().isEmpty()) {
                        deadlineError = "Выберите срок выполнения"
                        valid = false
                    }

                    if (!valid) return@Button

                    scope.launch {
                        try {
                            RetrofitClient.taskApi.createTask(
                                CreateTaskRequest(
                                    title = title.trim(),
                                    address = address.trim(),
                                    clientName = clientName.trim(),
                                    clientPhone = clientPhone.trim(),
                                    priority = priority,
                                    deadline = deadline.trim(),
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

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(message)
            }
        }
    }
}

@Composable
fun PriorityButton(
    value: String,
    label: String,
    current: String,
    onClick: () -> Unit
) {
    val selected = current == value

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
        Text(label)
    }
}