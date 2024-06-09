package com.ajou.helpt.network.model

import java.time.LocalDate

data class ExercisePosting(
    val gymEquipmentId: Int,
    val count: Int,
    val setNumber: Int,
    val weight: Int,
    val recordTime: String,
    val successRate: Int,
    val comment: String,
    val snapshotFile : String?
)
