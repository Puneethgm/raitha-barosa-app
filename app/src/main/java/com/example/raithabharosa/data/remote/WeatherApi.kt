package com.example.raitha_bharosa.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoGeocodingApi {
    @GET("v1/search")
    suspend fun searchLocations(
        @Query("name") name: String,
        @Query("count") count: Int = 1,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}

interface OpenMeteoForecastApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("hourly") hourly: String = "relativehumidity_2m",
        @Query("daily") daily: String = "weathercode,temperature_2m_max,temperature_2m_min,precipitation_probability_max",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoForecastResponse
}

data class GeocodingResponse(
    val results: List<LocationResult> = emptyList()
)

data class LocationResult(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val country: String? = null,
    @SerializedName("admin1") val region: String? = null
)

data class OpenMeteoForecastResponse(
    @SerializedName("current_weather") val currentWeather: CurrentWeather? = null,
    val hourly: HourlyWeather? = null,
    val daily: DailyWeather? = null
)

data class CurrentWeather(
    val temperature: Float = 0f,
    val weathercode: Int = 0,
    val time: String = "",
    val windspeed: Float = 0f
)

data class HourlyWeather(
    val time: List<String> = emptyList(),
    @SerializedName("relativehumidity_2m") val relativeHumidity2m: List<Int> = emptyList()
)

data class DailyWeather(
    val time: List<String> = emptyList(),
    val weathercode: List<Int> = emptyList(),
    @SerializedName("temperature_2m_max") val temperature2mMax: List<Float> = emptyList(),
    @SerializedName("temperature_2m_min") val temperature2mMin: List<Float> = emptyList(),
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int> = emptyList()
)
