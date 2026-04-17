package com.actum.app.network

data class LoginResponse(
    val token: String,
    val role: String,
    val userId: Long,
    val fullName: String
)