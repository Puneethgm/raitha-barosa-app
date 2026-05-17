package com.raithabharosa.hub.data.network

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val daily: List<Daily>,
    val currentTemp: Float = 0f,
    val currentHumidity: Float = 0f,
    val currentRainfall: Float = 0f,
    val currentWindSpeed: Float = 0f,
    val currentCondition: String = ""
)

data class OpenMeteoResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: OpenMeteoCurrent?,
    val daily: OpenMeteoDaily
)

data class OpenMeteoCurrent(
    @SerializedName("temperature_2m") val temperature: Double?,
    @SerializedName("relative_humidity_2m") val humidity: Double?,
    @SerializedName("precipitation") val precipitation: Double?,
    @SerializedName("wind_speed_10m") val windSpeed: Double?,
    @SerializedName("weather_code") val weatherCode: Int?
)

data class OpenMeteoDaily(
    @SerializedName("time") val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val temperature2mMax: List<Double>,
    @SerializedName("temperature_2m_min") val temperature2mMin: List<Double>,
    @SerializedName("precipitation_sum") val precipitationSum: List<Double>?,
    @SerializedName("wind_speed_10m_max") val windSpeed10mMax: List<Double>?,
    @SerializedName("relative_humidity_2m_max") val humidity2mMax: List<Double>?,
    @SerializedName("sunrise") val sunrise: List<String>? = null,
    @SerializedName("sunset") val sunset: List<String>? = null
)

data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Temp,
    val humidity: Int = 0,
    val rainfall: Double = 0.0,
    val windSpeed: Double = 0.0,
    val weather: List<WeatherDescription>
)

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
