package com.actum.app.model

data class TaskItem(
    val id: Long,
    val title: String,
    val address: String,
    val clientName: String,
    val status: String,
    val managerId: Long,
    val specialistId: Long?
)