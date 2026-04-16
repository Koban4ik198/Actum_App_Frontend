package com.actum.app.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface PdfApi {

    @GET("api/tasks/{id}/report/pdf")
    suspend fun downloadPdf(
        @Path("id") taskId: Long
    ): ResponseBody
}