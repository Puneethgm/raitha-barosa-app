package com.example.raitha_bharosa.domain.model

enum class WeatherType {
    SUNNY, CLOUDY, RAINY, STORMY
}

data class DayForecast(
    val dayIndex: Int, // 0 = today, 1 = tomorrow, etc.
    val weather: WeatherType,
    val tempMin: Float,
    val tempMax: Float,
    val recommendedAction: String, // "Sow", "Fertilize", "Irrigate", "Rest", "Harvest Prep"
    val heavyRainWarning: Boolean = false
)
