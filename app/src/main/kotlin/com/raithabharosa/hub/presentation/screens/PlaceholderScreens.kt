package com.raithabharosa.hub.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.raithabharosa.hub.R
import com.raithabharosa.hub.data.location.FusedLocationProvider
import com.raithabharosa.hub.data.network.*
import com.raithabharosa.hub.data.repository.WeatherRepository
import com.raithabharosa.hub.presentation.theme.GreenPrimary
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.location.Geocoder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun KrishiCalendarScreen() {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    var forecast by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isRealData by remember { mutableStateOf(false) }
    var locationName by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(12.9716) }
    var longitude by remember { mutableStateOf(77.5946) }

    val fetchWeatherData = suspend {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val svc = retrofit.create(WeatherService::class.java)
        val repo = WeatherRepository(svc)
        var lat = latitude
        var lon = longitude
        locationName = "Bengaluru"
        try {
            val loc = FusedLocationProvider(context).getLastLocation()
            if (loc != null) {
                lat = loc.latitude
                lon = loc.longitude
                latitude = lat
                longitude = lon

                // Get location name from coordinates
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        locationName = address.locality ?: address.adminArea ?: address.countryName ?: "Your Location"
                    }
                } catch (_: Throwable) {
                    locationName = "Your Location"
                }
            }
        } catch (_: Throwable) {}

        val res = repo.get7DayForecast(lat, lon)
        if (res.isSuccess) {
            forecast = res.getOrNull()
            isRealData = true
        } else {
            forecast = fallbackForecast(lat, lon)
            isRealData = false
        }
        isLoading = false
    }

    LaunchedEffect(Unit) {
        fetchWeatherData()
    }

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.seven_day_forecast), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                if (locationName.isNotBlank()) {
                    Text("📍 $locationName", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isRealData) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.2f)) {
                        Text(stringResource(R.string.live_indicator), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                IconButton(
                    onClick = {
                        isLoading = true
                        scope.launch { fetchWeatherData() }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh location and weather", tint = Color.White)
                }
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            val data = forecast
            if (data == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.forecast_error), color = colors.onBackground)
                }
            } else {
                // Current conditions banner
                Card(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenPrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(stringResource(R.string.now_label), color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            Text(
                                "${data.currentTemp.toInt()}°C",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp,
                                lineHeight = 44.sp
                            )
                            Text(data.currentCondition.replaceFirstChar { it.uppercase() }, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("💧 ${data.currentHumidity.toInt()}%", color = Color.White, fontSize = 14.sp)
                            Text("🌧 ${String.format("%.1f", data.currentRainfall)} mm", color = Color.White, fontSize = 14.sp)
                            Text("💨 ${String.format("%.1f", data.currentWindSpeed)} km/h", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }

                if (!isRealData) {
                    Text(
                        stringResource(R.string.showing_estimated_data),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(data.daily) { day ->
                        ForecastDayCard(day)
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ForecastDayCard(day: Daily) {
    val colors = MaterialTheme.colorScheme
    val date = Instant.ofEpochSecond(day.dt).atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now(ZoneId.systemDefault())
    val isToday = date == today
    val isTomorrow = date == today.plusDays(1)

    val dayLabel = when {
        isToday    -> LocalContext.current.getString(R.string.today_label)
        isTomorrow -> LocalContext.current.getString(R.string.tomorrow_label)
        else       -> date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
    val dateLabel = "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}"
    val icon = weatherEmoji(day.weather.firstOrNull()?.id ?: 0)
    val desc = day.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) GreenPrimary.copy(alpha = 0.1f) else colors.surface
        ),
        elevation = CardDefaults.cardElevation(if (isToday) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day + date
            Column(modifier = Modifier.width(100.dp)) {
                Text(
                    dayLabel,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 15.sp,
                    color = if (isToday) GreenPrimary else colors.onSurface
                )
                Text(dateLabel, fontSize = 12.sp, color = colors.onSurfaceVariant)
            }

            // Icon + description
            Text(icon, fontSize = 28.sp, modifier = Modifier.width(40.dp))
            Text(
                desc,
                fontSize = 13.sp,
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            // Temp range
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${day.temp.max.toInt()}°",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isToday) GreenPrimary else colors.onSurface
                )
                Text(
                    "${day.temp.min.toInt()}°",
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }

        // Extra row: humidity, rain, wind
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherStat("💧", "${day.humidity}%", R.string.humidity)
            WeatherStat("🌧", "${String.format("%.1f", day.rainfall)}mm", R.string.rain)
            WeatherStat("💨", "${String.format("%.0f", day.windSpeed)} km/h", R.string.wind)
        }
    }
}

@Composable
private fun WeatherStat(icon: String, value: String, labelRes: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(icon, fontSize = 12.sp)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun weatherEmoji(code: Int): String = when (code) {
    0            -> "☀️"
    1            -> "🌤"
    2            -> "⛅"
    3            -> "☁️"
    45, 48       -> "🌫"
    in 51..55    -> "🌦"
    in 61..65    -> "🌧"
    in 71..77    -> "❄️"
    in 80..82    -> "🌧"
    95, 96, 99   -> "⛈"
    else         -> "🌡"
}

@Composable
fun SeasonHistoryScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.season_history_title))
    }
}

private fun fallbackForecast(lat: Double, lon: Double): WeatherResponse {
    val today = LocalDate.now(ZoneId.systemDefault())
    val daily = (0 until 7).map { i ->
        val date = today.plusDays(i.toLong())
        Daily(
            dt = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
            sunrise = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
            sunset = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
            temp = Temp(day = 28.0 + i, min = 22.0 + i, max = 32.0 + i, night = 21.0 + i, eve = 27.0 + i, morn = 23.0 + i),
            humidity = 65 + i,
            rainfall = if (i % 3 == 0) 2.5 else 0.0,
            windSpeed = 10.0 + i,
            weather = listOf(WeatherDescription(0, "Clear", if (i % 2 == 0) "Clear sky" else "Partly cloudy", "01d"))
        )
    }
    return WeatherResponse(latitude = lat, longitude = lon, timezone = "Asia/Kolkata", daily = daily,
        currentTemp = 28f, currentHumidity = 65f, currentRainfall = 0f, currentWindSpeed = 10f, currentCondition = "Clear sky")
}
