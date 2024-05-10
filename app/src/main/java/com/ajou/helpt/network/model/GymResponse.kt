package com.ajou.helpt.network.model

import com.ajou.helpt.home.model.Gym
import com.google.gson.annotations.SerializedName

data class GymResponse(
    @SerializedName("status") val status : String,
    @SerializedName("data") val data : List<GymRegisteredInfo>
)
