package com.raithabharosa.hub.domain.engine
import com.raithabharosa.hub.data.model.SoilData
import com.raithabharosa.hub.data.model.WeatherData
import kotlin.random.Random

class DataGenerator {
    private val random = Random(System.currentTimeMillis())

    fun generateSoilData(): SoilData = SoilData(
        nitrogen = random.nextFloat() * 200,
        phosphorus = random.nextFloat() * 100,
        potassium = random.nextFloat() * 100,
        moisture = 10 + random.nextFloat() * 85,
        temperature = 15 + random.nextFloat() * 25,
        pH = 5.5f + random.nextFloat() * 2.5f,
        timestamp = System.currentTimeMillis()
    )

    fun generateWeatherData(): WeatherData {
        val conditions = listOf("Clear", "Cloudy", "Rainy")
        return WeatherData(
            temperature = 15 + random.nextFloat() * 25,
            humidity = 30 + random.nextFloat() * 70,
            rainfall = when { random.nextFloat() > 0.7f -> random.nextFloat() * 30 else -> 0f },
            windSpeed = random.nextFloat() * 25,
            condition = conditions.random(),
            timestamp = System.currentTimeMillis()
        )
    }

    fun generateRealisticSoilData(previousMoisture: Float? = null, previousTemp: Float? = null): SoilData {
        val moisture = when {
            previousMoisture != null -> {
                val change = (random.nextFloat() - 0.5f) * 10
                (previousMoisture + change).coerceIn(10f, 90f)
            }
            else -> 30 + random.nextFloat() * 50
        }
        val temperature = when {
            previousTemp != null -> {
                val change = (random.nextFloat() - 0.5f) * 3
                (previousTemp + change).coerceIn(10f, 40f)
            }
            else -> 20 + random.nextFloat() * 15
        }
        return SoilData(
            nitrogen = 60 + random.nextFloat() * 140,
            phosphorus = 20 + random.nextFloat() * 80,
            potassium = 30 + random.nextFloat() * 70,
            moisture = moisture, temperature = temperature,
            pH = 6.0f + random.nextFloat() * 1.5f,
            timestamp = System.currentTimeMillis()
        )
    }

    fun generateRealisticWeatherData(previousCondition: String? = null): WeatherData {
        val conditions = listOf("Clear", "Cloudy", "Rainy")
        val condition = when {
            previousCondition != null && random.nextFloat() > 0.3f -> previousCondition
            else -> conditions.random()
        }
        val rainfall = when (condition) {
            "Rainy" -> 5 + random.nextFloat() * 25
            "Cloudy" -> random.nextFloat() * 3
            else -> 0f
        }
        return WeatherData(
            temperature = 18 + random.nextFloat() * 20,
            humidity = 40 + random.nextFloat() * 50,
            rainfall = rainfall, windSpeed = random.nextFloat() * 20,
            condition = condition, timestamp = System.currentTimeMillis()
        )
    }
}
