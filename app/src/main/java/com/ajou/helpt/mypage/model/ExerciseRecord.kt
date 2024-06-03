package com.ajou.helpt.mypage.model

import java.time.LocalDate

data class ExerciseRecord(
    val equipmentName: String,
    val count: Int,
    val setNumber: Int,
    val weight: Int,
    val recordTime: String,
    val recordDate: LocalDate,
    val successRate: Int,
    val comment: String,
    val snapshotFile : String?
)
