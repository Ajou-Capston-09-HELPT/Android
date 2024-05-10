package com.ajou.helpt.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GymEquipment(
    val gymEquipmetId : Int,
    val equipmentName : String,
    val customCount : Int,
    val customSet : Int,
    val customWeight : Int
): Parcelable
