package com.ajou.helpt.auth

data class Member(
    val gymId : String?,
    val userName : String,
    val gender : String,
    val height : Int,
    val weight : Int,
    val kakaoId : String
)
