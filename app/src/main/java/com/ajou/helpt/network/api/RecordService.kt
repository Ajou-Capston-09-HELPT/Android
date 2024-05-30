package com.ajou.helpt.network.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface RecordService {
    @GET("records/detail/{date}")
    suspend fun getRecord(
        @Header("Authorization") accessToken : String,
        @Path("date") date : String
    ): Response<ResponseBody>
}
