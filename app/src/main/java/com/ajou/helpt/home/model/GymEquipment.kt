package com.ajou.helpt.home.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GymEquipment(
    val gymEquipmentId : Int,
    val equipmentName : String,
    val equipmentNameEng : String,
    val customCount : Int,
    val customSet : Int,
    val customWeight : Int
): Parcelable
