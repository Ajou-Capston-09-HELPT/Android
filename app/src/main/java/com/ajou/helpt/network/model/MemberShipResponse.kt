package com.ajou.helpt.network.model

import com.ajou.helpt.home.model.Membership
import com.google.gson.annotations.SerializedName

data class MemberShipResponse(
    @SerializedName("status") val status : String,
    @SerializedName("data") val data : Membership
)
