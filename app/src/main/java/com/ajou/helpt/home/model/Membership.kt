package com.ajou.helpt.home.model

import com.google.gson.annotations.SerializedName

data class Membership(
    val gymId: Int,
    val membershipId: Int,
    val attendanceDate: Int,
    val startDate: String,
    val endDate: String
)
