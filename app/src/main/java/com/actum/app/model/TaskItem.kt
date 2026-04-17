package com.actum.app.model

data class TaskItem(
    val id: Long,
    val title: String,
    val address: String,
    val clientName: String,
    val clientPhone: String?,
    val priority: String?,
    val deadline: String?,
    val status: String,
    val managerId: Long,
    val managerFullName: String?,
    val specialistId: Long?,
    val specialistFullName: String?
)