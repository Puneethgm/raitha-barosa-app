package com.raithabharosa.hub.data.repository

import com.raithabharosa.hub.data.network.Daily
import com.raithabharosa.hub.data.network.Temp
import com.raithabharosa.hub.data.network.WeatherDescription
import com.raithabharosa.hub.data.network.WeatherResponse
import com.raithabharosa.hub.data.network.WeatherService
import com.raithabharosa.hub.data.network.OpenMeteoResponse
import java.time.LocalDate
import java.time.ZoneId

class WeatherRepository(private val service: WeatherService) {
    suspend fun get7DayForecast(lat: Double, lon: Double, lang: String = "en"): Result<WeatherResponse> {
        return try {
            val res = service.sevenDayForecast(lat, lon)
            Result.success(mapOpenMeteo(res))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    private fun mapOpenMeteo(response: OpenMeteoResponse): WeatherResponse {
        val daily = response.daily
        val mapped = daily.time.indices.map { index ->
            val date = LocalDate.parse(daily.time[index])
            val dt = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
            Daily(
                dt = dt,
                sunrise = dt,
                sunset = dt,
                temp = Temp(
                    day = daily.temperature2mMax.getOrNull(index) ?: 0.0,
                    min = daily.temperature2mMin.getOrNull(index) ?: 0.0,
                    max = daily.temperature2mMax.getOrNull(index) ?: 0.0,
                    night = daily.temperature2mMin.getOrNull(index) ?: 0.0,
                    eve = daily.temperature2mMax.getOrNull(index) ?: 0.0,
                    morn = daily.temperature2mMin.getOrNull(index) ?: 0.0
                ),
                weather = listOf(WeatherDescription(
                    id = daily.weatherCode.getOrNull(index) ?: 0,
                    main = weatherMain(daily.weatherCode.getOrNull(index) ?: 0),
                    description = weatherDescription(daily.weatherCode.getOrNull(index) ?: 0),
                    icon = weatherIcon(daily.weatherCode.getOrNull(index) ?: 0)
                ))
            )
        }
        return WeatherResponse(
            latitude = response.latitude,
            longitude = response.longitude,
            timezone = response.timezone,
            daily = mapped
        )
    }

    private fun weatherMain(code: Int): String = when (code) {
        0 -> "Clear"
        1, 2, 3 -> "Clouds"
        45, 48 -> "Fog"
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Rain"
        71, 73, 75, 77, 85, 86 -> "Snow"
        else -> "Weather"
    }

    private fun weatherDescription(code: Int): String = when (code) {
        0 -> "Clear sky"
        1 -> "Mainly clear"
        2 -> "Partly cloudy"
        3 -> "Overcast"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        80, 81, 82 -> "Rain showers"
        else -> "Forecast"
    }

    private fun weatherIcon(code: Int): String = when (code) {
        0 -> "01d"
        1 -> "02d"
        2 -> "03d"
        3 -> "04d"
        45, 48 -> "50d"
        51, 53, 55 -> "09d"
        61, 63, 65, 80, 81, 82 -> "10d"
        71, 73, 75, 77, 85, 86 -> "13d"
        else -> "03d"
    }
}
