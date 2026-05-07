package com.example.raitha_bharosa.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.raitha_bharosa.R
import com.example.raitha_bharosa.domain.model.Farmer
import com.example.raitha_bharosa.ui.components.BottomNavBar
import com.example.raitha_bharosa.ui.theme.BackgroundLightLeaf
import com.example.raitha_bharosa.ui.theme.ErrorRed
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsContent(
        uiState = uiState,
        onNavigate = { navController.navigate(it) },
        onLanguageChange = { language ->
            viewModel.updateLanguage(language)
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
        },
        onCropChange = viewModel::updateCrop,
        onVillageChange = viewModel::updateVillage,
        onClearDataClick = viewModel::openClearDialog,
        onLogoutClick = {
            viewModel.logout()
            navController.navigate("auth") {
                popUpTo(0) { inclusive = true }
            }
        },
        onClearDataConfirm = {
            viewModel.clearAllData()
            navController.navigate("onboarding") {
                popUpTo(0) { inclusive = true }
            }
        },
        onClearDataCancel = viewModel::closeClearDialog
    )
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onNavigate: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onCropChange: (String) -> Unit,
    onVillageChange: (String) -> Unit,
    onClearDataClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onClearDataConfirm: () -> Unit,
    onClearDataCancel: () -> Unit
) {
    var cropExpanded by remember { mutableStateOf(false) }
    val crops = listOf("Paddy", "Ragi", "Sugarcane", "Maize", "Groundnut", "Cotton")

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = "settings",
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
        } else if (uiState.farmer == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLightLeaf),
                contentAlignment = Alignment.Center
            ) {
                Text("No farmer data found")
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
                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Language Setting
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Language",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onLanguageChange("en") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("English")
                            }
                            OutlinedButton(
                                onClick = { onLanguageChange("kn") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ಕನ್ನಡ")
                            }
                        }
                    }
                }

                // Crop Setting
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Primary Crop",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = uiState.farmer.primaryCrop,
                            onValueChange = {},
                            label = { Text("Crop") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .clickable { cropExpanded = true },
                            readOnly = true,
                            enabled = false
                        )

                        DropdownMenu(
                            expanded = cropExpanded,
                            onDismissRequest = { cropExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            crops.forEach { crop ->
                                DropdownMenuItem(
                                    text = { Text(crop) },
                                    onClick = {
                                        onCropChange(crop)
                                        cropExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Village Setting
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Village/City",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = uiState.farmer.village,
                            onValueChange = onVillageChange,
                            label = { Text("Village/City") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        )
                    }
                }

                // Clear Data
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Danger Zone",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )

                        OutlinedButton(
                            onClick = onClearDataClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text(stringResource(R.string.clear_data), color = ErrorRed)
                        }

                        OutlinedButton(
                            onClick = onLogoutClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text("Logout", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                if (uiState.error != null) {
                    Text(
                        text = uiState.error,
                        color = ErrorRed,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        if (uiState.isClearDialogOpen) {
            AlertDialog(
                onDismissRequest = onClearDataCancel,
                title = { Text("Clear All Data?") },
                text = { Text("This action cannot be undone. All farmer data will be deleted.") },
                confirmButton = {
                    Button(onClick = onClearDataConfirm) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onClearDataCancel) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    RaithabharosTheme {
        SettingsContent(
            uiState = SettingsUiState(
                farmer = Farmer(
                    id = 1,
                    name = "Ramesh",
                    village = "Bangalore",
                    primaryCrop = "Paddy",
                    language = "en"
                ),
                isLoading = false
            ),
            onNavigate = {},
            onLanguageChange = {},
            onCropChange = {},
            onVillageChange = {},
            onClearDataClick = {},
            onLogoutClick = {},
            onClearDataConfirm = {},
            onClearDataCancel = {}
        )
    }
}
