package com.actum.app.network

import com.actum.app.model.TaskItem
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskActionApi {

    @POST("api/tasks/{id}/cancel")
    suspend fun cancelTask(
        @Path("id") taskId: Long,
        @Query("reason") reason: String
    ): TaskItem

    @POST("api/tasks/{id}/complete")
    suspend fun completeTask(
        @Path("id") taskId: Long
    ): TaskItem
}