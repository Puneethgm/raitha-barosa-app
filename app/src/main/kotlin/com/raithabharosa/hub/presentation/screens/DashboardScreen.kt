package com.raithabharosa.hub.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.raithabharosa.hub.R
import com.raithabharosa.hub.data.model.CropType
import com.raithabharosa.hub.data.model.ScheduledAction
import com.raithabharosa.hub.data.model.SowingStatus
import com.raithabharosa.hub.presentation.theme.GreenPrimary
import com.raithabharosa.hub.presentation.theme.NeutralWhite
import com.raithabharosa.hub.presentation.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiState = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Color(0xFF2E6B49), Color(0xFF4B8C67))))
                    .padding(24.dp)
            ) {
                Column {
                    Text(stringResource(R.string.dashboard_title_long), style = MaterialTheme.typography.headlineLarge, color = NeutralWhite)
                    Text(uiState.farmerProfile?.name ?: stringResource(R.string.farmer_name_label), style = MaterialTheme.typography.bodyMedium, color = NeutralWhite.copy(alpha = 0.88f))
                }
                IconButton(onClick = { viewModel.refreshData(uiState.selectedCropType) }, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(Icons.Default.Refresh, null, tint = NeutralWhite)
                }
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ElevatedCard(shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(stringResource(R.string.select_crop_type), style = MaterialTheme.typography.titleMedium)
                        // Dropdown selector for crop types (easier to select and localize)
                        var expanded by remember { mutableStateOf(false) }
                        val selectedLabel = cropLabel(uiState.selectedCropType)
                        Box {
                            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                                Text(selectedLabel, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                                Text("v", color = MaterialTheme.colorScheme.onSurface)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                CropType.values().forEach { crop ->
                                    DropdownMenuItem(
                                        text = { Text(cropLabel(crop)) },
                                        onClick = { viewModel.selectCropType(crop); expanded = false }
                                    )
                                }
                            }
                        }
                    }
                }

                ElevatedCard(shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.can_grow_now), style = MaterialTheme.typography.titleMedium)
                        Text(growNowLabel(uiState.sowingStatus), style = MaterialTheme.typography.displaySmall, color = GreenPrimary)
                        Text(stringResource(R.string.sowing_index_label_long) + " ${String.format("%.0f%%", uiState.sowingIndex)}")
                        Text(statusLabel(uiState.sowingStatus))
                    }
                }

                ElevatedCard(shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.crop_advice), style = MaterialTheme.typography.titleMedium)
                        Text(uiState.cropAdvice?.note ?: uiState.recommendation)
                        Text(stringResource(R.string.fertilizer_label) + ": ${uiState.cropAdvice?.fertilizer ?: "-"}")
                        Text(stringResource(R.string.quantity_label) + ": ${uiState.cropAdvice?.quantity ?: "-"}")
                    }
                }

                // Diagnostic block to help see the soil/weather values used to compute sowing index
                ElevatedCard(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(stringResource(R.string.diagnostics), style = MaterialTheme.typography.titleSmall)
                        Text(stringResource(R.string.sowing_index_label_long) + ": ${String.format("%.0f%%", uiState.sowingIndex)} - ${statusLabel(uiState.sowingStatus)}")
                        uiState.currentSoilData?.let { s ->
                            Text(stringResource(R.string.soil_data_label) + " - N:${s.nitrogen}, P:${s.phosphorus}, K:${s.potassium}, Moist:${s.moisture}, Temp:${s.temperature}, pH:${s.pH}")
                        } ?: Text(stringResource(R.string.soil_data_label) + ": -")
                        uiState.currentWeatherData?.let { w ->
                            Text(stringResource(R.string.weather_data_label) + " - Temp:${w.temperature}°C, Rain:${w.rainfall}mm, Wind:${w.windSpeed}km/h, Cond:${w.condition}")
                        } ?: Text(stringResource(R.string.weather_data_label) + ": -")
                    }
                }

                uiState.currentWeatherData?.let { w ->
                    ElevatedCard(shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(stringResource(R.string.weather_label), style = MaterialTheme.typography.titleMedium)
                            Text("${w.temperature}°C", style = MaterialTheme.typography.displaySmall, color = GreenPrimary)
                            Text(w.condition)
                            Text(stringResource(R.string.wind_label, String.format("%.1f", w.windSpeed)))
                        }
                    }
                }

                if (uiState.scheduledActions.isNotEmpty()) {
                    Text(stringResource(R.string.scheduled_actions_label), style = MaterialTheme.typography.titleMedium, color = GreenPrimary)
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(uiState.scheduledActions) { action: ScheduledAction ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(action.title, style = MaterialTheme.typography.bodyLarge)
                                    action.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                    Text(java.time.Instant.ofEpochMilli(action.epochMillis).atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString(), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun cropLabel(cropType: CropType): String = when (cropType) {
    CropType.SUGARCANE -> androidx.compose.ui.res.stringResource(R.string.crop_sugarcane)
    CropType.RAGI -> androidx.compose.ui.res.stringResource(R.string.crop_ragi)
    CropType.PADDY -> androidx.compose.ui.res.stringResource(R.string.crop_paddy)
}

@Composable
private fun statusLabel(status: SowingStatus): String = when (status) {
    SowingStatus.GREEN -> androidx.compose.ui.res.stringResource(R.string.ready_to_sow)
    SowingStatus.AMBER -> androidx.compose.ui.res.stringResource(R.string.monitor_conditions)
    SowingStatus.RED -> androidx.compose.ui.res.stringResource(R.string.not_ready)
}

@Composable
private fun growNowLabel(status: SowingStatus): String = when (status) {
    SowingStatus.GREEN -> androidx.compose.ui.res.stringResource(R.string.yes)
    SowingStatus.AMBER -> androidx.compose.ui.res.stringResource(R.string.maybe)
    SowingStatus.RED -> androidx.compose.ui.res.stringResource(R.string.no)
}