package com.ajou.helpt.network.api

import com.ajou.helpt.home.model.Gym
import com.ajou.helpt.network.model.GymEquipResponse
import com.ajou.helpt.network.model.GymResponse
import com.ajou.helpt.network.model.OneGymResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GymService {
    @GET("gyms")
    suspend fun searchGyms(
        @Header("Authorization") accessToken: String,
        @Query("name") name: String
    ): Response<GymResponse>

    @GET("gyms/{gymId}")
    suspend fun getOneGym(
        @Header("Authorization") accessToken: String,
        @Path(value = "gymId") gymId : Int
    ) : Response<OneGymResponse>

    @GET("gyms/{gymId}/gym-equipments")
    suspend fun getGymEquips(
        @Header("Authorization") accessToken: String,
        @Path(value = "gymId") gymId : Int
    ): Response<GymEquipResponse>
}