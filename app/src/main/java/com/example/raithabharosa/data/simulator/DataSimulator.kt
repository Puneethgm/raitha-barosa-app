package com.example.raitha_bharosa.data.simulator

import com.example.raitha_bharosa.domain.model.DayForecast
import com.example.raitha_bharosa.domain.model.WeatherType
import javax.inject.Inject
import kotlin.random.Random

class DataSimulator @Inject constructor() {

    fun generateMoistureLevel(): Float {
        return Random.nextFloat() * 30 + 10 // 10% to 40%
    }

    fun generateTemperature(): Float {
        return Random.nextFloat() * 20 + 18 // 18.0 to 38.0 Celsius
    }

    fun generateHumidity(): Float {
        return Random.nextFloat() * 55 + 40 // 40% to 95%
    }

    fun generate7DayForecast(): List<DayForecast> {
        val forecasts = mutableListOf<DayForecast>()

        for (day in 0..6) {
            val weather = randomWeatherType()
            val tempMin = Random.nextFloat() * 5 + 18
            val tempMax = tempMin + Random.nextFloat() * 5 + 5
            val action = when (weather) {
                WeatherType.SUNNY -> "Sow"
                WeatherType.CLOUDY -> "Fertilize"
                WeatherType.RAINY -> "Rest"
                WeatherType.STORMY -> "Rest"
            }

            val heavyRainWarning = day == 3 && (weather == WeatherType.RAINY || weather == WeatherType.STORMY)

            forecasts.add(
                DayForecast(
                    dayIndex = day,
                    weather = weather,
                    tempMin = tempMin,
                    tempMax = tempMax,
                    recommendedAction = action,
                    heavyRainWarning = heavyRainWarning
                )
            )

            // If day 3 has heavy rain, add warning to day 2
            if (heavyRainWarning && day == 3 && forecasts.size > 2) {
                forecasts[2] = forecasts[2].copy(heavyRainWarning = true)
            }
        }

        return forecasts
    }

    fun randomWeatherType(): WeatherType {
        return when (Random.nextInt(100)) {
            in 0..49 -> WeatherType.SUNNY
            in 50..69 -> WeatherType.CLOUDY
            in 70..89 -> WeatherType.RAINY
            else -> WeatherType.STORMY
        }
    }
}
