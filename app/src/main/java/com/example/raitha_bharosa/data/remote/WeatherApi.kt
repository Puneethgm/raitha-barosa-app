package com.example.raitha_bharosa.data.remote

import com.example.raitha_bharosa.domain.model.DayForecast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherApi @Inject constructor() {
    
    suspend fun getWeatherForecast(location: String): List<DayForecast> {
        // Mock implementation - replace with actual API call
        return listOf(
            DayForecast(
                date = System.currentTimeMillis(),
                temperature = 25.0,
                humidity = 60.0,
                description = "Partly cloudy",
                precipitation = 0.0
            )
        )
    }
    
    suspend fun getCurrentWeather(location: String): DayForecast {
        // Mock implementation - replace with actual API call
        return DayForecast(
            date = System.currentTimeMillis(),
            temperature = 25.0,
            humidity = 60.0,
            description = "Partly cloudy",
            precipitation = 0.0
        )
    }
}
