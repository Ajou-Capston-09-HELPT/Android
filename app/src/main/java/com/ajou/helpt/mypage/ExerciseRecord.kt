package com.ajou.helpt.mypage

import java.time.LocalDate

data class ExerciseRecord(
    val equipmentId: Int,
    val count: Int,
    val setNumber: Int,
    val weight: Int,
    val recordDate: LocalDate,
    val successRate: Int,
    val comment: String,
    val snapshotFile: String
)
