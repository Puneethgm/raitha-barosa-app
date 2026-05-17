package com.raithabharosa.hub.data.repository

import com.raithabharosa.hub.data.network.*
import java.time.LocalDate
import java.time.ZoneId

class WeatherRepository(private val service: WeatherService) {

    suspend fun get7DayForecast(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val res = service.sevenDayForecast(lat, lon)
            Result.success(mapOpenMeteo(res))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    private fun mapOpenMeteo(response: OpenMeteoResponse): WeatherResponse {
        val daily = response.daily
        val mapped = daily.time.indices.map { i ->
            val date = LocalDate.parse(daily.time[i])
            val dt = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
            Daily(
                dt = dt,
                sunrise = dt,
                sunset = dt,
                temp = Temp(
                    day   = daily.temperature2mMax.getOrNull(i) ?: 0.0,
                    min   = daily.temperature2mMin.getOrNull(i) ?: 0.0,
                    max   = daily.temperature2mMax.getOrNull(i) ?: 0.0,
                    night = daily.temperature2mMin.getOrNull(i) ?: 0.0,
                    eve   = daily.temperature2mMax.getOrNull(i) ?: 0.0,
                    morn  = daily.temperature2mMin.getOrNull(i) ?: 0.0
                ),
                humidity  = (daily.humidity2mMax?.getOrNull(i) ?: 0.0).toInt(),
                rainfall  = daily.precipitationSum?.getOrNull(i) ?: 0.0,
                windSpeed = daily.windSpeed10mMax?.getOrNull(i) ?: 0.0,
                weather = listOf(
                    WeatherDescription(
                        id          = daily.weatherCode.getOrNull(i) ?: 0,
                        main        = weatherMain(daily.weatherCode.getOrNull(i) ?: 0),
                        description = weatherDescription(daily.weatherCode.getOrNull(i) ?: 0),
                        icon        = weatherIcon(daily.weatherCode.getOrNull(i) ?: 0)
                    )
                )
            )
        }

        val cur = response.current
        return WeatherResponse(
            latitude         = response.latitude,
            longitude        = response.longitude,
            timezone         = response.timezone,
            daily            = mapped,
            currentTemp      = cur?.temperature?.toFloat() ?: mapped.firstOrNull()?.temp?.day?.toFloat() ?: 0f,
            currentHumidity  = cur?.humidity?.toFloat() ?: mapped.firstOrNull()?.humidity?.toFloat() ?: 0f,
            currentRainfall  = cur?.precipitation?.toFloat() ?: mapped.firstOrNull()?.rainfall?.toFloat() ?: 0f,
            currentWindSpeed = cur?.windSpeed?.toFloat() ?: mapped.firstOrNull()?.windSpeed?.toFloat() ?: 0f,
            currentCondition = weatherDescription(cur?.weatherCode ?: mapped.firstOrNull()?.weather?.firstOrNull()?.id ?: 0)
        )
    }

    private fun weatherMain(code: Int): String = when (code) {
        0            -> "Clear"
        1, 2, 3      -> "Clouds"
        45, 48       -> "Fog"
        in 51..65    -> "Rain"
        in 71..77    -> "Snow"
        in 80..82    -> "Rain"
        95, 96, 99   -> "Thunderstorm"
        else         -> "Weather"
    }

    private fun weatherDescription(code: Int): String = when (code) {
        0  -> "Clear sky"
        1  -> "Mainly clear"
        2  -> "Partly cloudy"
        3  -> "Overcast"
        45 -> "Fog"
        48 -> "Icy fog"
        51 -> "Light drizzle"
        53 -> "Drizzle"
        55 -> "Heavy drizzle"
        61 -> "Light rain"
        63 -> "Rain"
        65 -> "Heavy rain"
        71 -> "Light snow"
        73 -> "Snow"
        75 -> "Heavy snow"
        80 -> "Light showers"
        81 -> "Rain showers"
        82 -> "Heavy showers"
        95 -> "Thunderstorm"
        96 -> "Thunderstorm with hail"
        99 -> "Heavy thunderstorm"
        else -> "Forecast"
    }

    private fun weatherIcon(code: Int): String = when (code) {
        0            -> "01d"
        1            -> "02d"
        2            -> "03d"
        3            -> "04d"
        45, 48       -> "50d"
        in 51..55    -> "09d"
        in 61..65    -> "10d"
        in 71..77    -> "13d"
        in 80..82    -> "10d"
        95, 96, 99   -> "11d"
        else         -> "03d"
    }
}
