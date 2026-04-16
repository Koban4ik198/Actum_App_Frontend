package com.actum.app.network

data class CreateTaskRequest(
    val title: String,
    val address: String,
    val clientName: String,
    val clientPhone: String,
    val priority: String,
    val deadline: String,
    val managerId: Long
)