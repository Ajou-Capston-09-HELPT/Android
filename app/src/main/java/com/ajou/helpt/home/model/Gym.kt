package com.ajou.helpt.home.model

import android.os.Parcelable
import com.ajou.helpt.network.model.GymEquipment
import com.ajou.helpt.network.model.GymRegisteredInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class Gym(
    val gymRegisteredInfo: GymRegisteredInfo,
    val equipList: List<GymEquipment>,
    val gymProduct : List<GymProduct>
) : Parcelable
