package com.ajou.helpt.home.view.fragment

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface QrService {
    @POST("qr")
    suspend fun getQr(
        @Header("Authorization") accessToken: String
    ): Response<ResponseBody>
}