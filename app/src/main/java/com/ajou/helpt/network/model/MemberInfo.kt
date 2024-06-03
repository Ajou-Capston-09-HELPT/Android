package com.ajou.helpt.network.model

import java.time.LocalDate

data class MemberInfo(
    val gymId: Int,
    val userName : String,
    val gender : String,
    val height : Int,
    val weight : Int,
    val kakaoId : String,
//    val profileImage: String,
//    val birthDate: LocalDate
)
