package com.ajou.helpt.home.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GymAddress(
    val fullAddress : String,
    val detailAddress : String,
    val latitude : String,
    val longitude: String
):Parcelable
