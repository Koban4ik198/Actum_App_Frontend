package com.actum.app.network

data class CreateTaskRequest(
    val title: String,
    val address: String,
    val clientName: String,
    val managerId: Long
)