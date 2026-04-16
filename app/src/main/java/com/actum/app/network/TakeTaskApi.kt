package com.actum.app.network

import com.actum.app.model.TaskItem
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TakeTaskApi {

    @POST("api/tasks/{id}/take")
    suspend fun takeTask(
        @Path("id") taskId: Long,
        @Query("specialistId") specialistId: Long
    ): TaskItem
}