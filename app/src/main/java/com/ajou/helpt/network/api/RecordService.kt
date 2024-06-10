package com.ajou.helpt.network.api

import com.ajou.helpt.network.model.DailyRecordsResponse
import com.ajou.helpt.network.model.ExercisePosting
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RecordService {
    @GET("records/detail/{date}")
    suspend fun getRecord(
        @Header("Authorization") accessToken : String,
        @Path("date") date : String
    ): Response<DailyRecordsResponse>


    @Multipart
    @POST("records")
    suspend fun postRecord(
        @Header("Authorization") accessToken : String,
        @Part("recordRequest") record : ExercisePosting,
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>
}
