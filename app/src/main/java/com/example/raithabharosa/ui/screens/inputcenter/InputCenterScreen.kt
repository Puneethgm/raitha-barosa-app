package com.example.raitha_bharosa.ui.screens.inputcenter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.raitha_bharosa.ui.components.BottomNavBar
import com.example.raitha_bharosa.ui.components.SowingIndexWheel
import com.example.raitha_bharosa.ui.theme.BackgroundLightLeaf
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme

@Composable
fun InputCenterScreen(
    navController: NavController,
    viewModel: InputCenterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    InputCenterContent(
        uiState = uiState,
        onNitrogenChange = viewModel::updateNitrogen,
        onPhosphorusChange = viewModel::updatePhosphorus,
        onPotassiumChange = viewModel::updatePotassium,
        onMoistureChange = viewModel::updateMoisture,
        onTemperatureChange = viewModel::updateTemperature,
        onSave = viewModel::saveReading,
        onNavigate = { navController.navigate(it) },
        snackbarHostState = snackbarHostState,
        onClearSuccess = viewModel::clearSuccess
    )
}

@Composable
private fun InputCenterContent(
    uiState: InputCenterUiState,
    onNitrogenChange: (Float) -> Unit,
    onPhosphorusChange: (Float) -> Unit,
    onPotassiumChange: (Float) -> Unit,
    onMoistureChange: (String) -> Unit,
    onTemperatureChange: (Float) -> Unit,
    onSave: () -> Unit,
    onNavigate: (String) -> Unit,
    snackbarHostState: androidx.compose.material3.SnackbarHostState,
    onClearSuccess: () -> Unit
) {
    var moistureExpanded by remember { mutableStateOf(false) }
    val moistureOptions = listOf("Dry", "Moist", "Wet", "Waterlogged")

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = "input_center",
                onNavigate = onNavigate
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLightLeaf)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Soil Input Center",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Nitrogen Slider
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Soil Nitrogen (N)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${uiState.nitrogen.toInt()} kg/ha",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = uiState.nitrogen,
                    onValueChange = onNitrogenChange,
                    valueRange = 0f..100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            // Phosphorus Slider
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Soil Phosphorus (P)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${uiState.phosphorus.toInt()} kg/ha",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = uiState.phosphorus,
                    onValueChange = onPhosphorusChange,
                    valueRange = 0f..100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            // Potassium Slider
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Soil Potassium (K)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${uiState.potassium.toInt()} kg/ha",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = uiState.potassium,
                    onValueChange = onPotassiumChange,
                    valueRange = 0f..100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            // Moisture Dropdown
            OutlinedTextField(
                value = uiState.moisture,
                onValueChange = {},
                label = { Text("Field Moisture") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .clickable { moistureExpanded = true },
                readOnly = true,
                enabled = false
            )

            DropdownMenu(
                expanded = moistureExpanded,
                onDismissRequest = { moistureExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                moistureOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onMoistureChange(option)
                            moistureExpanded = false
                        }
                    )
                }
            }

            // Temperature Input
            OutlinedTextField(
                value = uiState.temperature.toString(),
                onValueChange = { value ->
                    value.toFloatOrNull()?.let { onTemperatureChange(it) }
                },
                label = { Text("Temperature (°C)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
            )

            // Sowing Index Display
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Calculated Sowing Index",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${uiState.sowingIndex}%",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Save Button
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 16.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.height(24.dp))
                } else {
                    Text("Save Reading")
                }
            }

            if (uiState.isSaved) {
                Text(
                    text = "✓ Reading saved successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun InputCenterScreenPreview() {
    RaithabharosTheme {
        InputCenterContent(
            uiState = InputCenterUiState(),
            onNitrogenChange = {},
            onPhosphorusChange = {},
            onPotassiumChange = {},
            onMoistureChange = {},
            onTemperatureChange = {},
            onSave = {},
            onNavigate = {},
            snackbarHostState = androidx.compose.material3.SnackbarHostState(),
            onClearSuccess = {}
        )
    }
}
