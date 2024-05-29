package com.ajou.helpt.network.model

import com.ajou.helpt.home.model.NoticeData
import com.google.gson.annotations.SerializedName

data class NoticeResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<NoticeData>
)
