package com.example.raitha_bharosa.domain.model

data class DayForecast(
    val date: Long,
    val temperature: Double,
    val humidity: Double,
    val description: String,
    val precipitation: Double
)
