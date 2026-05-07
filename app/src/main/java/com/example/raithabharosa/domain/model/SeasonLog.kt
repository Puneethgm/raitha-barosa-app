package com.example.raitha_bharosa.domain.model

data class SeasonLog(
    val id: Int = 0,
    val farmerId: Int,
    val date: String,
    val crop: String,
    val sowingIndex: Int,
    val actionTaken: String,
    val notes: String
)
