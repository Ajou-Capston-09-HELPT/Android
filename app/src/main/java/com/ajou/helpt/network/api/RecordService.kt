package com.ajou.helpt.network.api

import com.ajou.helpt.auth.Member
import com.ajou.helpt.mypage.model.ExerciseRecord
import com.ajou.helpt.network.model.DailyRecordsResponse
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


    @POST("records")
    suspend fun postRecord(
        @Header("Authorization") accessToken : String,
        @Body record : ExercisePosting,
    ): Response<ResponseBody>
}
