package com.example.raitha_bharosa.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.raitha_bharosa.R
import com.example.raitha_bharosa.domain.model.DayForecast
import com.example.raitha_bharosa.domain.model.WeatherType
import com.example.raitha_bharosa.ui.components.BottomNavBar
import com.example.raitha_bharosa.ui.components.DayForecastCard
import com.example.raitha_bharosa.ui.theme.BackgroundLightLeaf
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme

@Composable
fun KrishiCalendarScreen(
    navController: NavController,
    viewModel: KrishiCalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    KrishiCalendarContent(
        uiState = uiState,
        onNavigate = { navController.navigate(it) },
        onRefresh = viewModel::refresh
    )
}

@Composable
private fun KrishiCalendarContent(
    uiState: KrishiCalendarUiState,
    onNavigate: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = "krishi_calendar",
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLightLeaf),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLightLeaf),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLightLeaf)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                item {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.seven_day_calendar),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.retry))
                        }
                    }
                }

                items(uiState.forecasts) { forecast ->
                    DayForecastCard(forecast)
                }
            }
        }
    }
}

@Preview
@Composable
fun KrishiCalendarScreenPreview() {
    RaithabharosTheme {
        KrishiCalendarContent(
            uiState = KrishiCalendarUiState(
                forecasts = listOf(
                    DayForecast(0, WeatherType.SUNNY, 24f, 31f, "Sow"),
                    DayForecast(1, WeatherType.CLOUDY, 23f, 30f, "Fertilize"),
                    DayForecast(2, WeatherType.RAINY, 22f, 28f, "Rest", true)
                )
            ),
            onNavigate = {},
            onRefresh = {}
        )
    }
}
