package com.ajou.helpt.home.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GymProduct(
    val productId : Int,
    val months : Int,
    val price : Int
):Parcelable
