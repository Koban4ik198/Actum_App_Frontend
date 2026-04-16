package com.actum.app.network

import com.actum.app.model.TaskItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TaskApi {

    @GET("api/tasks")
    suspend fun getTasks(): List<TaskItem>

    @POST("api/tasks")
    suspend fun createTask(
        @Body request: CreateTaskRequest
    ): TaskItem
}