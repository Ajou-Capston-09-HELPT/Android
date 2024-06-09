package com.ajou.helpt.network.model

import com.ajou.helpt.mypage.model.ExerciseRecord
import com.google.gson.annotations.SerializedName
import retrofit2.Response

data class DailyRecordsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data : List<ExerciseRecord>
)
