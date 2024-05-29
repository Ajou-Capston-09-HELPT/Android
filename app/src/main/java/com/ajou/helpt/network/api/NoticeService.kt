package com.ajou.helpt.network.api

import com.ajou.helpt.network.model.NoticeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NoticeService {
    @GET("notices")
    suspend fun getNotices(
        @Header("Authorization") accessToken : String,
        @Query("gymId") id: Int
    ): Response<NoticeResponse>
}