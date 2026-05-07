package com.example.raitha_bharosa.data.repository

import android.net.Uri
import com.example.raitha_bharosa.data.remote.CurrentWeather
import com.example.raitha_bharosa.data.remote.DailyWeather
import com.example.raitha_bharosa.data.remote.GeocodingResponse
import com.example.raitha_bharosa.data.remote.LocationResult
import com.example.raitha_bharosa.data.remote.OpenMeteoForecastApi
import com.example.raitha_bharosa.data.remote.OpenMeteoForecastResponse
import com.example.raitha_bharosa.data.remote.OpenMeteoGeocodingApi
import com.example.raitha_bharosa.data.simulator.DataSimulator
import com.example.raitha_bharosa.domain.model.DayForecast
import com.example.raitha_bharosa.domain.model.WeatherType
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val geocodingApi: OpenMeteoGeocodingApi,
    private val forecastApi: OpenMeteoForecastApi,
    private val dataSimulator: DataSimulator
) {

    suspend fun getCurrentConditions(locationName: String): WeatherConditions {
        return try {
            val location = resolveLocation(locationName)
            val forecast = forecastApi.getForecast(location.latitude, location.longitude)
            val currentWeather = forecast.currentWeather
            val humidity = currentHumidity(forecast, currentWeather)

            WeatherConditions(
                temperature = currentWeather?.temperature ?: dataSimulator.generateTemperature(),
                humidity = humidity ?: dataSimulator.generateHumidity(),
                estimatedMoisture = estimateMoisture(currentWeather, humidity),
                locationName = formatLocationName(location)
            )
        } catch (_: Exception) {
            WeatherConditions(
                temperature = dataSimulator.generateTemperature(),
                humidity = dataSimulator.generateHumidity(),
                estimatedMoisture = dataSimulator.generateMoistureLevel(),
                locationName = locationName.ifBlank { "Unknown location" }
            )
        }
    }

    suspend fun get7DayForecast(locationName: String): List<DayForecast> {
        return try {
            val location = resolveLocation(locationName)
            val forecast = forecastApi.getForecast(location.latitude, location.longitude)
            mapDailyForecasts(forecast.daily)
        } catch (_: Exception) {
            dataSimulator.generate7DayForecast()
        }
    }

    fun getSimulatedWeather() = dataSimulator.generate7DayForecast()
    fun getSimulatedMoisture() = dataSimulator.generateMoistureLevel()
    fun getSimulatedTemperature() = dataSimulator.generateTemperature()
    fun getSimulatedHumidity() = dataSimulator.generateHumidity()

    private suspend fun resolveLocation(locationName: String): LocationResult {
        val query = Uri.encode(locationName.ifBlank { "Bengaluru" })
        val response: GeocodingResponse = geocodingApi.searchLocations(query)
        return response.results.firstOrNull() ?: throw IllegalStateException("No location found")
    }

    private fun currentHumidity(forecast: OpenMeteoForecastResponse, currentWeather: CurrentWeather?): Float? {
        val currentTime = currentWeather?.time ?: return null
        val hourly = forecast.hourly ?: return null
        val index = hourly.time.indexOf(currentTime)
        if (index == -1) return null
        return hourly.relativeHumidity2m.getOrNull(index)?.toFloat()
    }

    private fun estimateMoisture(currentWeather: CurrentWeather?, humidity: Float?): Float {
        val baseHumidity = humidity ?: 65f
        val rainBoost = when (currentWeather?.weathercode) {
            in 51..67, in 80..99 -> 10f
            else -> 0f
        }
        return ((baseHumidity / 2f) + rainBoost).coerceIn(10f, 40f)
    }

    private fun formatLocationName(location: LocationResult): String {
        return buildString {
            append(location.name)
            location.region?.takeIf { it.isNotBlank() }?.let { append(", ").append(it) }
            location.country?.takeIf { it.isNotBlank() }?.let { append(", ").append(it) }
        }
    }

    private fun mapDailyForecasts(daily: DailyWeather?): List<DayForecast> {
        if (daily == null) return dataSimulator.generate7DayForecast()

        return daily.time.indices.take(7).map { index ->
            val weatherCode = daily.weathercode.getOrNull(index) ?: 0
            val weatherType = mapWeatherCode(weatherCode)
            val tempMin = daily.temperature2mMin.getOrNull(index) ?: 0f
            val tempMax = daily.temperature2mMax.getOrNull(index) ?: 0f
            val rainChance = daily.precipitationProbabilityMax.getOrNull(index) ?: 0

            DayForecast(
                dayIndex = index,
                weather = weatherType,
                tempMin = tempMin,
                tempMax = tempMax,
                recommendedAction = recommendedActionFor(weatherType),
                heavyRainWarning = rainChance >= 60 || weatherType == WeatherType.RAINY || weatherType == WeatherType.STORMY
            )
        }
    }

    private fun mapWeatherCode(code: Int): WeatherType {
        return when (code) {
            0, 1 -> WeatherType.SUNNY
            2, 3, 45, 48 -> WeatherType.CLOUDY
            in 51..67, in 80..99 -> WeatherType.RAINY
            else -> WeatherType.CLOUDY
        }
    }

    private fun recommendedActionFor(weatherType: WeatherType): String {
        return when (weatherType) {
            WeatherType.SUNNY -> "Sow"
            WeatherType.CLOUDY -> "Fertilize"
            WeatherType.RAINY -> "Rest"
            WeatherType.STORMY -> "Rest"
        }
    }
}

data class WeatherConditions(
    val temperature: Float,
    val humidity: Float,
    val estimatedMoisture: Float,
    val locationName: String
)
