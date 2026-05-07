package com.example.raitha_bharosa.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raitha_bharosa.data.repository.FarmerRepository
import com.example.raitha_bharosa.domain.model.Farmer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val name: String = "",
    val village: String = "",
    val crop: String = "",
    val language: String = "en",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val farmerRepository: FarmerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateVillage(village: String) {
        _uiState.value = _uiState.value.copy(village = village)
    }

    fun updateCrop(crop: String) {
        _uiState.value = _uiState.value.copy(crop = crop)
    }

    fun updateLanguage(language: String) {
        _uiState.value = _uiState.value.copy(language = language)
    }

    fun submitOnboarding() {
        val state = _uiState.value

        when {
            state.name.isBlank() -> {
                _uiState.value = state.copy(error = "Please enter your name")
                return
            }
            state.village.isBlank() -> {
                _uiState.value = state.copy(error = "Please enter village/city")
                return
            }
            state.crop.isBlank() -> {
                _uiState.value = state.copy(error = "Please select a crop")
                return
            }
        }

        _uiState.value = state.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val farmer = Farmer(
                    name = state.name,
                    village = state.village,
                    primaryCrop = state.crop,
                    language = state.language
                )
                farmerRepository.insertFarmer(farmer)
                _uiState.value = state.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = "Error saving farmer data: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
