package com.ajou.helpt.home.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Gym(
    val gymRegisteredInfo: GymRegisteredInfo,
    val equipList: List<GymEquipment>,
    val gymProduct : List<GymProduct>
) : Parcelable
