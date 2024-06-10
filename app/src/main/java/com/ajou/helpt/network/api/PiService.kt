package com.ajou.helpt.network.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface PiService {
    @GET("top_accuracy_image")
    @Streaming
    suspend fun getImg(): Response<ResponseBody>
}