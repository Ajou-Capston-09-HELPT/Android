package com.ajou.helpt.network.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GymAdmissionService {
    @POST("gym-admissions/gyms/{gymId}")
    suspend fun postAdmission(
        @Header("Authorization") accessToken: String,
        @Path(value = "gymId") id : Int
    ): Response<ResponseBody>

}