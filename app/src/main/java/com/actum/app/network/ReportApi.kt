package com.actum.app.network

import retrofit2.http.GET
import retrofit2.http.Path

interface ReportApi {

    @GET("api/tasks/{id}/report")
    suspend fun getReport(
        @Path("id") taskId: Long
    ): TaskReport
}