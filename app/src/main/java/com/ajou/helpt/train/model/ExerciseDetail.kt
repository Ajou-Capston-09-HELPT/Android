package com.ajou.helpt.train.model

data class ExerciseDetail(
    val exerciseId: Int,
    val exerciseDescription: String,
    val exerciseMethod: List<String>,
    val topImage: String,
    val videoUrl: String
)
