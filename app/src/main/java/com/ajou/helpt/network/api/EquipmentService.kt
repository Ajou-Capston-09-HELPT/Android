package com.ajou.helpt.network.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface EquipmentService {
    @GET("equipments/{equipmentId}")
    suspend fun getEquipment(
        @Header("Authorization") accessToken : String,
        @Path("equipmentId") equipmentId : Int
    ): Response<ResponseBody>

}