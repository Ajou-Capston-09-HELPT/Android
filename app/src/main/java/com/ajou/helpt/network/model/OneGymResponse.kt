package com.ajou.helpt.network.model

import com.google.gson.annotations.SerializedName

data class OneGymResponse(
    @SerializedName("status") val status : String,
    @SerializedName("data") val data : GymRegisteredInfo
)
