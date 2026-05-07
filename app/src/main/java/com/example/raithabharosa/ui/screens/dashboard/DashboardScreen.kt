package com.example.raitha_bharosa.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.raitha_bharosa.R
import com.example.raitha_bharosa.ui.components.BottomNavBar
import com.example.raitha_bharosa.ui.components.SoilChip
import com.example.raitha_bharosa.ui.components.SowingIndexWheel
import com.example.raitha_bharosa.ui.theme.BackgroundLightLeaf
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardContent(
        uiState = uiState,
        onNavigate = { navController.navigate(it) },
        onRefresh = viewModel::refresh
    )
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onNavigate: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = "dashboard",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLightLeaf)
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Header with greeting
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.welcome),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.hello_farmer, uiState.farmer?.name ?: ""),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.retry))
                    }
                }

                // Sowing Index Wheel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SowingIndexWheel(
                        index = uiState.sowingIndex,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Today's Action
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.today_action),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = when (uiState.todayAction) {
                                "Wait" -> stringResource(R.string.action_wait)
                                "Get Ready" -> stringResource(R.string.action_ready)
                                "Sow Now" -> stringResource(R.string.action_sow)
                                else -> uiState.todayAction
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Current Conditions
                Text(
                    text = stringResource(R.string.current_conditions),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SoilChip(
                        label = stringResource(R.string.temperature),
                        value = "${uiState.temperature.toInt()}°C",
                        icon = "🌡️",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    SoilChip(
                        label = stringResource(R.string.humidity),
                        value = "${uiState.humidity.toInt()}%",
                        icon = "💧",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    SoilChip(
                        label = stringResource(R.string.moisture),
                        value = "${uiState.moisture.toInt()}%",
                        icon = "🌱",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }

                // Quick Stats
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.quick_stats),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.last_sowing, "Not recorded"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.days_since_fertilizer, 0),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DashboardScreenPreview() {
    RaithabharosTheme {
        DashboardContent(
            uiState = DashboardUiState(),
            onNavigate = {},
            onRefresh = {}
        )
    }
}
