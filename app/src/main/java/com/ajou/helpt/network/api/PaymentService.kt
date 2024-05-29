package com.ajou.helpt.network.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentService {
    @POST("payment/ready/{productId}")
    suspend fun postPayment(
        @Header("Authorization") accessToken : String,
        @Path(value = "productId") id : Int
    ): Response<ResponseBody>
}