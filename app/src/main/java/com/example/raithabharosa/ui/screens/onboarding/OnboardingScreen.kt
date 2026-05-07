package com.example.raitha_bharosa.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.raitha_bharosa.ui.theme.BackgroundLightLeaf
import com.example.raitha_bharosa.ui.theme.RaithabharosTheme

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isSuccess) {
        navController.navigate("dashboard") {
            popUpTo("onboarding") { inclusive = true }
        }
    }

    OnboardingContent(
        uiState = uiState,
        onNameChange = viewModel::updateName,
        onVillageChange = viewModel::updateVillage,
        onCropChange = viewModel::updateCrop,
        onLanguageChange = viewModel::updateLanguage,
        onSubmit = viewModel::submitOnboarding,
        onErrorDismiss = viewModel::clearError
    )
}

@Composable
private fun OnboardingContent(
    uiState: OnboardingUiState,
    onNameChange: (String) -> Unit,
    onVillageChange: (String) -> Unit,
    onCropChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    var cropExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }

    val crops = listOf("Paddy", "Ragi", "Sugarcane", "Maize", "Groundnut", "Cotton")
    val languages = listOf("English", "Kannada")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLightLeaf)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Welcome",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Raitha-Bharosa Hub",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text("Enter your name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = uiState.village,
            onValueChange = onVillageChange,
            label = { Text("Enter village/city") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Crop Dropdown
        OutlinedTextField(
            value = uiState.crop,
            onValueChange = {},
            label = { Text("Select primary crop") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .clickable { cropExpanded = true },
            readOnly = true,
            enabled = false
        )

        DropdownMenu(
            expanded = cropExpanded,
            onDismissRequest = { cropExpanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
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

        Spacer(modifier = Modifier.height(16.dp))

        // Language Dropdown
        OutlinedTextField(
            value = uiState.language,
            onValueChange = {},
            label = { Text("Select language") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .clickable { languageExpanded = true },
            readOnly = true,
            enabled = false
        )

        DropdownMenu(
            expanded = languageExpanded,
            onDismissRequest = { languageExpanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        onLanguageChange(language)
                        languageExpanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = uiState.name.isNotEmpty() && uiState.village.isNotEmpty() && uiState.crop.isNotEmpty() && uiState.language.isNotEmpty()
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    RaithabharosTheme {
        OnboardingContent(
            uiState = OnboardingUiState(
                name = "",
                village = "",
                crop = "Paddy",
                language = "English"
            ),
            onNameChange = {},
            onVillageChange = {},
            onCropChange = {},
            onLanguageChange = {},
            onSubmit = {},
            onErrorDismiss = {}
        )
    }
}
