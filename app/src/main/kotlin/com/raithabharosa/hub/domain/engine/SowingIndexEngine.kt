package com.raithabharosa.hub.domain.engine
import com.raithabharosa.hub.data.model.*
import kotlin.math.max
import kotlin.math.min

class SowingIndexEngine {
    fun calculateSowingIndex(
        soilData: SoilData, weatherData: WeatherData, cropType: CropType
    ): SowingIndexResult {
        val criteria = getCropCriteria(cropType)
        val moistureScore = calculateMoistureScore(soilData.moisture, criteria)
        val temperatureScore = calculateTemperatureScore(soilData.temperature, criteria)
        val npkScore = calculateNPKScore(soilData, criteria)
        val weatherScore = calculateWeatherScore(weatherData, criteria)
        val phScore = calculatePhScore(soilData.pH, criteria)

        val totalIndex = moistureScore * 0.25f + temperatureScore * 0.20f +
                        npkScore * 0.25f + weatherScore * 0.20f + phScore * 0.10f
        val clampedIndex = max(0f, min(100f, totalIndex))
        val status = when {
            clampedIndex >= 70 -> SowingStatus.GREEN
            clampedIndex >= 50 -> SowingStatus.AMBER
            else -> SowingStatus.RED
        }

        return SowingIndexResult(
            index = clampedIndex, status = status,
            recommendation = generateRecommendation(status, soilData, weatherData, cropType),
            nextCheckIn = 24 * 60 * 60 * 1000
        )
    }

    private fun calculateMoistureScore(moisture: Float, criteria: CropCriteria): Float {
        return when {
            moisture < criteria.minMoisture -> (moisture / criteria.minMoisture) * 50
            moisture <= criteria.optimalMoisture -> 100f
            moisture <= criteria.maxMoisture -> 100 - ((moisture - criteria.optimalMoisture) / (criteria.maxMoisture - criteria.optimalMoisture)) * 40
            else -> max(0f, 60 - (moisture - criteria.maxMoisture) * 2)
        }
    }

    private fun calculateTemperatureScore(temp: Float, criteria: CropCriteria): Float {
        return when {
            temp < criteria.minTemp -> (temp / criteria.minTemp) * 50
            temp <= criteria.optimalTemp -> 100f
            temp <= criteria.maxTemp -> 100 - ((temp - criteria.optimalTemp) / (criteria.maxTemp - criteria.optimalTemp)) * 40
            else -> max(0f, 60 - (temp - criteria.maxTemp))
        }
    }

    private fun calculateNPKScore(soilData: SoilData, criteria: CropCriteria): Float {
        val nScore = when {
            soilData.nitrogen < criteria.minN -> (soilData.nitrogen / criteria.minN) * 60
            soilData.nitrogen <= criteria.optimalN -> 100f
            else -> max(50f, 100 - (soilData.nitrogen - criteria.optimalN) * 0.5f)
        }
        val pScore = when {
            soilData.phosphorus < criteria.minP -> (soilData.phosphorus / criteria.minP) * 60
            soilData.phosphorus <= criteria.optimalP -> 100f
            else -> max(50f, 100 - (soilData.phosphorus - criteria.optimalP) * 0.5f)
        }
        val kScore = when {
            soilData.potassium < criteria.minK -> (soilData.potassium / criteria.minK) * 60
            soilData.potassium <= criteria.optimalK -> 100f
            else -> max(50f, 100 - (soilData.potassium - criteria.optimalK) * 0.5f)
        }
        return (nScore + pScore + kScore) / 3
    }

    private fun calculateWeatherScore(weather: WeatherData, criteria: CropCriteria): Float {
        val rainfallScore = when {
            weather.rainfall < 2 -> 100f
            weather.rainfall <= 10 -> 90f
            weather.rainfall > 20 -> 40f
            else -> 70f
        }
        val windScore = when {
            weather.windSpeed > 30 -> 40f
            weather.windSpeed > 15 -> 70f
            else -> 100f
        }
        val conditionScore = when (weather.condition.lowercase()) {
            "clear" -> 70f
            "cloudy" -> 85f
            "rainy" -> 60f
            else -> 50f
        }
        return (rainfallScore + windScore + conditionScore) / 3
    }

    private fun calculatePhScore(ph: Float, criteria: CropCriteria): Float {
        return when {
            ph < criteria.minPH -> (ph / criteria.minPH) * 70
            ph <= criteria.optimalPH -> 100f
            ph <= criteria.maxPH -> 100 - ((ph - criteria.optimalPH) / (criteria.maxPH - criteria.optimalPH)) * 30
            else -> max(40f, 70 - (ph - criteria.maxPH) * 10)
        }
    }

    private fun generateRecommendation(
        status: SowingStatus, soilData: SoilData, weatherData: WeatherData, cropType: CropType
    ): String = when (status) {
        SowingStatus.GREEN -> "Conditions are ideal for sowing. Begin sowing today!"
        SowingStatus.AMBER -> "Conditions acceptable. Monitor and retry tomorrow if needed."
        SowingStatus.RED -> "Conditions not suitable. Wait for improved conditions."
    }

    private fun getCropCriteria(cropType: CropType): CropCriteria = when (cropType) {
        CropType.SUGARCANE -> CropCriteria(60f, 75f, 85f, 18f, 28f, 35f, 100f, 150f, 40f, 60f, 40f, 80f, 6f, 7f, 7.5f)
        CropType.RAGI -> CropCriteria(30f, 50f, 70f, 15f, 25f, 32f, 40f, 60f, 20f, 30f, 20f, 40f, 5.5f, 6.5f, 7f)
        CropType.PADDY -> CropCriteria(70f, 85f, 95f, 18f, 28f, 35f, 60f, 100f, 30f, 50f, 30f, 60f, 6.5f, 7f, 7.5f)
        CropType.COTTON -> CropCriteria(40f, 60f, 75f, 18f, 27f, 33f, 40f, 70f, 20f, 30f, 30f, 60f, 6f, 6.8f, 7.5f)
        CropType.CORN -> CropCriteria(45f, 65f, 80f, 15f, 25f, 32f, 60f, 100f, 25f, 40f, 30f, 60f, 6f, 7f, 7.5f)
        CropType.WHEAT -> CropCriteria(35f, 55f, 70f, 10f, 20f, 28f, 50f, 80f, 20f, 30f, 25f, 50f, 6f, 7f, 7.5f)
        CropType.SOYBEAN -> CropCriteria(45f, 60f, 75f, 15f, 25f, 30f, 50f, 80f, 20f, 30f, 25f, 50f, 6f, 6.8f, 7.5f)
        CropType.GROUNDNUT -> CropCriteria(40f, 55f, 70f, 18f, 26f, 32f, 40f, 70f, 15f, 25f, 20f, 40f, 5.5f, 6.5f, 7f)
        CropType.SUNFLOWER -> CropCriteria(35f, 50f, 65f, 12f, 22f, 30f, 40f, 70f, 20f, 30f, 25f, 50f, 6f, 7f, 8f)
        CropType.CHILI -> CropCriteria(45f, 60f, 75f, 20f, 28f, 35f, 40f, 70f, 20f, 30f, 20f, 40f, 6f, 7f, 7.5f)
        CropType.TOMATO -> CropCriteria(45f, 60f, 75f, 20f, 28f, 35f, 50f, 80f, 25f, 35f, 30f, 60f, 6f, 6.8f, 7f)
        CropType.ONION -> CropCriteria(40f, 55f, 70f, 12f, 22f, 28f, 40f, 70f, 15f, 25f, 20f, 40f, 6f, 6.8f, 7.5f)
    }
}

data class CropCriteria(
    val minMoisture: Float, val optimalMoisture: Float, val maxMoisture: Float,
    val minTemp: Float, val optimalTemp: Float, val maxTemp: Float,
    val minN: Float, val optimalN: Float,
    val minP: Float, val optimalP: Float,
    val minK: Float, val optimalK: Float,
    val minPH: Float, val optimalPH: Float, val maxPH: Float
)
