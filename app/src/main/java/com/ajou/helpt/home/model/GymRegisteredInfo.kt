package com.ajou.helpt.home.model

import android.os.Parcelable
import com.ajou.helpt.home.model.GymAddress
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GymRegisteredInfo(
    @SerializedName("gymId") val gymId : Int,
    @SerializedName("address") val address : GymAddress,
    @SerializedName("gymName") val gymName : String,
    @SerializedName("status") val status : String
): Parcelable
