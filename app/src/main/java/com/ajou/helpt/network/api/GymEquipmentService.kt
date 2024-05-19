package com.ajou.helpt.network.api

import com.ajou.helpt.network.model.OneGymEquipResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GymEquipmentService {
    @GET("gym-equipments/{gymEquipmentId}")
    suspend fun getSelectedTrain(
        @Header("Authorization") accessToken: String,
        @Path(value = "gymEquipmentId") id : Int
    ): Response<OneGymEquipResponse>
}