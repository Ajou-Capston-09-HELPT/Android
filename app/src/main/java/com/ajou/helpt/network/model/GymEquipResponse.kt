package com.ajou.helpt.network.model

import com.ajou.helpt.home.model.GymEquipment
import com.google.gson.annotations.SerializedName

data class GymEquipResponse(
    @SerializedName("status") val status : String,
    @SerializedName("data") val data : List<GymEquipment>
)
