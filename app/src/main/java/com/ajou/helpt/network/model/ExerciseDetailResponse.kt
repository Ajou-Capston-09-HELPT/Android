package com.ajou.helpt.network.model

import com.ajou.helpt.train.model.ExerciseDetail
import com.google.gson.annotations.SerializedName

data class ExerciseDetailResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: ExerciseDetail
)
