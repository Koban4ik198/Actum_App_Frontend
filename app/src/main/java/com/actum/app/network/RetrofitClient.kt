package com.actum.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val taskApi: TaskApi = retrofit.create(TaskApi::class.java)
    val takeTaskApi: TakeTaskApi = retrofit.create(TakeTaskApi::class.java)
    val taskActionApi: TaskActionApi = retrofit.create(TaskActionApi::class.java)
    val reportApi: ReportApi = retrofit.create(ReportApi::class.java)
    val pdfApi: PdfApi = retrofit.create(PdfApi::class.java)
}