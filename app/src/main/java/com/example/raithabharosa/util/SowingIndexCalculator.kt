package com.example.raitha_bharosa.util

object SowingIndexCalculator {

    fun calculateIndex(
        moisture: Float,
        temperature: Float,
        humidity: Float,
        crop: String,
        nitrogen: Float = 50f,
        phosphorus: Float = 50f,
        potassium: Float = 50f
    ): Int {
        val profile = cropProfile(crop)

        val score =
            nutrientScore(nitrogen, profile.nitrogenTarget, profile.nutrientTolerance) * 0.30f +
            nutrientScore(phosphorus, profile.phosphorusTarget, profile.nutrientTolerance) * 0.15f +
            nutrientScore(potassium, profile.potassiumTarget, profile.nutrientTolerance) * 0.15f +
            climateScore(moisture, profile.moistureTarget, profile.moistureTolerance) * 0.20f +
            climateScore(temperature, profile.temperatureTarget, profile.temperatureTolerance) * 0.10f +
            climateScore(humidity, profile.humidityTarget, profile.humidityTolerance) * 0.10f

        return score.toInt().coerceIn(0, 100)
    }

    private fun cropProfile(crop: String): CropProfile {
        return when (crop) {
            "Paddy" -> CropProfile(60f, 40f, 50f, 28f, 70f, 28f, 30f, 18f, 15f, 15f)
            "Ragi" -> CropProfile(40f, 35f, 40f, 22f, 55f, 24f, 30f, 18f, 15f, 15f)
            "Sugarcane" -> CropProfile(70f, 45f, 60f, 30f, 65f, 30f, 30f, 18f, 15f, 15f)
            "Maize" -> CropProfile(55f, 40f, 45f, 24f, 60f, 26f, 30f, 18f, 15f, 15f)
            "Groundnut" -> CropProfile(35f, 45f, 35f, 20f, 55f, 24f, 30f, 18f, 15f, 15f)
            "Cotton" -> CropProfile(50f, 40f, 45f, 22f, 50f, 30f, 30f, 18f, 15f, 15f)
            else -> CropProfile(50f, 40f, 45f, 25f, 60f, 26f, 30f, 18f, 15f, 15f)
        }
    }

    private fun nutrientScore(value: Float, target: Float, tolerance: Float): Float {
        val distance = kotlin.math.abs(value - target)
        return ((1f - (distance / tolerance)).coerceIn(0f, 1f) * 100f)
    }

    private fun climateScore(value: Float, target: Float, tolerance: Float): Float {
        val distance = kotlin.math.abs(value - target)
        return ((1f - (distance / tolerance)).coerceIn(0f, 1f) * 100f)
    }

    private data class CropProfile(
        val nitrogenTarget: Float,
        val phosphorusTarget: Float,
        val potassiumTarget: Float,
        val moistureTarget: Float,
        val humidityTarget: Float,
        val temperatureTarget: Float,
        val nutrientTolerance: Float,
        val moistureTolerance: Float,
        val temperatureTolerance: Float,
        val humidityTolerance: Float
    )
}
