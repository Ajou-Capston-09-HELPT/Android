package com.ajou.helpt.home.model

data class PayInfo(
    val period : String, // 서버와 숫자, 문자열 중 어느 쪽으로 줄건지 얘기 후 변경 (두 데이터 모두 해당)
    val price : String // 가능하면 price는 숫자 형태로 받는 게 좋을 듯
)
