package com.ajou.helpt.home.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Gym(
    val name : String,
    val address : String,
    val equipList : List<String>?
) : Parcelable
