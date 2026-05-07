package com.example.raitha_bharosa.domain.model

data class SoilReading(
    val id: Int = 0,
    val farmerId: Int,
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val moisture: String, // "Dry", "Moist", "Wet", "Waterlogged"
    val temperature: Float,
    val timestamp: Long = System.currentTimeMillis()
)
