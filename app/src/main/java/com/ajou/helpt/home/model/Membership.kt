package com.ajou.helpt.home.model

import com.google.gson.annotations.SerializedName

data class Membership(
    val membershipId: Int,
    val userId: Int,
    val gymId: Int,
    val attendanceDate: Int,
    val isAttendToday: Boolean,
    val startDate: String,
    val endDate: String
)
