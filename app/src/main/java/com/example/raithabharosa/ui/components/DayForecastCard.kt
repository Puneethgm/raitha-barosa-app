package com.example.raitha_bharosa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raitha_bharosa.R
import com.example.raitha_bharosa.domain.model.DayForecast
import com.example.raitha_bharosa.domain.model.WeatherType
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme
import com.example.raitha_bharosa.ui.theme.ErrorRed

@Composable
fun DayForecastCard(
    forecast: DayForecast,
    modifier: Modifier = Modifier
) {
    val dayLabel = when (forecast.dayIndex) {
        0 -> stringResource(R.string.day_today)
        1 -> stringResource(R.string.day_tomorrow)
        else -> stringResource(R.string.day_format, forecast.dayIndex)
    }

    val weatherEmoji = when (forecast.weather) {
        WeatherType.SUNNY -> "☀️"
        WeatherType.CLOUDY -> "⛅"
        WeatherType.RAINY -> "🌧️"
        WeatherType.STORMY -> "⛈️"
    }

    val cardColor = if (forecast.heavyRainWarning) {
        ErrorRed.copy(alpha = 0.1f)
    } else {
        Color.White
    }

    val borderColor = if (forecast.heavyRainWarning) {
        ErrorRed
    } else {
        Color.Transparent
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (forecast.heavyRainWarning) {
                Text(
                    text = stringResource(R.string.heavy_rain_warning),
                    color = ErrorRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dayLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when (forecast.recommendedAction) {
                            "Sow" -> stringResource(R.string.action_sow)
                            "Fertilize" -> stringResource(R.string.action_fertilize)
                            "Irrigate" -> stringResource(R.string.action_irrigate)
                            "Wait" -> stringResource(R.string.action_wait)
                            "Rest" -> stringResource(R.string.action_rest)
                            "Harvest Prep" -> stringResource(R.string.action_harvest)
                            else -> forecast.recommendedAction
                        },
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = weatherEmoji,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "${forecast.tempMin.toInt()}°C - ${forecast.tempMax.toInt()}°C",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DayForecastCardPreview() {
    RaithabharosTheme {
        Column(Modifier.background(Color.White).padding(16.dp)) {
            DayForecastCard(
                DayForecast(
                    dayIndex = 0,
                    weather = WeatherType.SUNNY,
                    tempMin = 24f,
                    tempMax = 31f,
                    recommendedAction = "Sow",
                    heavyRainWarning = false
                )
            )
            DayForecastCard(
                DayForecast(
                    dayIndex = 1,
                    weather = WeatherType.RAINY,
                    tempMin = 22f,
                    tempMax = 28f,
                    recommendedAction = "Rest",
                    heavyRainWarning = true
                )
            )
        }
    }
}
