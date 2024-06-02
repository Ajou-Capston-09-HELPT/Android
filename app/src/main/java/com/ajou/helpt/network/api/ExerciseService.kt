package com.ajou.helpt.network.api

import com.ajou.helpt.network.model.ExerciseDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ExerciseService {
    @GET("exercises/{exercisedId}")
    suspend fun getExerciseInfo(
        @Header("Authorization") accessToken: String,
        @Path(value = "exercisedId") exercisedId: Int
    ): Response<ExerciseDetailResponse>

}