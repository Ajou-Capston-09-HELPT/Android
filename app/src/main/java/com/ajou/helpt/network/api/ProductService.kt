package com.ajou.helpt.network.api

import com.ajou.helpt.network.model.GymProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ProductService {
    @GET("products/{gymId}")
    suspend fun getGymProducts(
        @Header("Authorization") accessToken : String,
        @Path(value = "gymId") gymId : Int
    ): Response<GymProductResponse>
}