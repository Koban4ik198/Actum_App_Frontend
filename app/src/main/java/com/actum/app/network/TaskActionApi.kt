package com.actum.app.network

import com.actum.app.model.TaskItem
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class CompleteTaskRequest(
    val taskId: Long,
    val data: String
)

interface TaskActionApi {

    @POST("api/tasks/{id}/cancel")
    suspend fun cancelTask(
        @Path("id") taskId: Long,
        @Query("reason") reason: String
    ): TaskItem

    @POST("api/tasks/complete")
    suspend fun completeTask(
        @Body request: CompleteTaskRequest
    ): TaskItem
}