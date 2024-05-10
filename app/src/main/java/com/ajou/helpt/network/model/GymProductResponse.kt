package com.ajou.helpt.network.model

import com.ajou.helpt.home.model.GymProduct
import com.google.gson.annotations.SerializedName

data class GymProductResponse(
    @SerializedName("status") val status : String,
    @SerializedName("data") val data : List<GymProduct>
)
