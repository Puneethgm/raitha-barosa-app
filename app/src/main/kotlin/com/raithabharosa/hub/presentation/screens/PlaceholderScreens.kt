package com.raithabharosa.hub.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.raithabharosa.hub.data.network.Daily
import com.raithabharosa.hub.data.network.WeatherResponse
import com.raithabharosa.hub.data.network.WeatherService
import com.raithabharosa.hub.data.repository.WeatherRepository
// no viewmodel imports needed here
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.ui.res.stringResource
import com.raithabharosa.hub.R
import com.raithabharosa.hub.data.location.rememberLocationPermissionRequest
import com.raithabharosa.hub.data.network.Temp
import com.raithabharosa.hub.data.network.WeatherDescription
import java.time.LocalDate
import java.time.ZoneId
import com.raithabharosa.hub.data.storage.SessionManager
import androidx.compose.runtime.collectAsState



@Composable
fun KrishiCalendarScreen() {
    val defaultLat = 12.9716
    val defaultLon = 77.5946
    var forecast by remember { mutableStateOf<WeatherResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val (requestPermission, permissionGranted) = rememberLocationPermissionRequest()
    val fused = com.raithabharosa.hub.data.location.FusedLocationProvider(androidx.compose.ui.platform.LocalContext.current)
    LaunchedEffect(Unit) {
        try {
            val retrofit = Retrofit.Builder().baseUrl("https://api.open-meteo.com/").addConverterFactory(GsonConverterFactory.create()).build()
            val svc = retrofit.create(WeatherService::class.java)
            val repo = WeatherRepository(svc)
            var lat = defaultLat
            var lon = defaultLon
            try {
                val loc = fused.getLastLocation()
                if (loc != null) { lat = loc.latitude; lon = loc.longitude }
            } catch (_: Throwable) {}
            val res = repo.get7DayForecast(lat, lon)
            if (res.isSuccess) {
                forecast = res.getOrNull()
            } else {
                forecast = fallbackForecast(lat, lon)
            }
        } catch (_: Throwable) {
            forecast = fallbackForecast(defaultLat, defaultLon)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text(text = stringResource(R.string.seven_day_forecast), modifier = Modifier.padding(16.dp))
        if (forecast == null) {
            Text(text = stringResource(R.string.loading), modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(forecast!!.daily) { day ->
                    ForecastRow(day)
                }
            }
            Text(text = stringResource(R.string.showing_simulated_forecast), modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun ForecastRow(day: Daily) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val iconUrl = "https://openweathermap.org/img/wn/${day.weather.firstOrNull()?.icon}@2x.png"
            AsyncImage(model = iconUrl, contentDescription = null, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                val context = androidx.compose.ui.platform.LocalContext.current
                val desc = day.weather.firstOrNull()?.description ?: ""
                Text(text = java.time.Instant.ofEpochSecond(day.dt).atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString())
                Text(text = mapWeatherDescription(desc))
                Text(text = "${day.temp.min}°C - ${day.temp.max}°C")
            }
        }
    }
}

@Composable
private fun mapWeatherDescription(desc: String): String {
    val d = desc.lowercase()
    val res = when {
        d.contains("clear") -> R.string.weather_clear
        d.contains("cloud") || d.contains("overcast") -> R.string.weather_cloudy
        d.contains("rain") || d.contains("drizzle") || d.contains("shower") -> R.string.weather_rainy
        d.contains("fog") || d.contains("mist") -> R.string.weather_fog
        d.contains("snow") -> R.string.weather_snow
        d.contains("shower") -> R.string.weather_rain_showers
        else -> R.string.weather_unknown
    }
    return stringResource(res)
}

@Composable
fun SeasonHistoryScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.season_history_title))
    }
}

private fun fallbackForecast(lat: Double, lon: Double): WeatherResponse {
    val today = LocalDate.now(ZoneId.systemDefault())
    val daily = (0 until 7).map { index ->
        val date = today.plusDays(index.toLong())
        Daily(
            dt = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
            sunrise = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
            sunset = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
            temp = Temp(
                day = 28.0 + index,
                min = 22.0 + index,
                max = 32.0 + index,
                night = 21.0 + index,
                eve = 27.0 + index,
                morn = 23.0 + index
            ),
            weather = listOf(
                WeatherDescription(
                    id = 0,
                    main = "Clear",
                    description = if (index % 2 == 0) "Clear sky" else "Partly cloudy",
                    icon = "01d"
                )
            )
        )
    }
    return WeatherResponse(latitude = lat, longitude = lon, timezone = "Asia/Kolkata", daily = daily)
}
