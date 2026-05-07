package com.example.raitha_bharosa.ui.screens.inputcenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raitha_bharosa.data.repository.FarmerRepository
import com.example.raitha_bharosa.data.repository.SoilRepository
import com.example.raitha_bharosa.domain.model.SoilReading
import com.example.raitha_bharosa.util.SowingIndexCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InputCenterUiState(
    val nitrogen: Float = 50f,
    val phosphorus: Float = 50f,
    val potassium: Float = 50f,
    val moisture: String = "Moist",
    val temperature: Float = 25f,
    val sowingIndex: Int = 50,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class InputCenterViewModel @Inject constructor(
    private val farmerRepository: FarmerRepository,
    private val soilRepository: SoilRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InputCenterUiState())
    val uiState: StateFlow<InputCenterUiState> = _uiState

    private var _recentReadings: Flow<List<SoilReading>> = emptyFlow()
    val recentReadings: Flow<List<SoilReading>>
        get() = _recentReadings

    init {
        loadRecentReadings()
    }

    private fun loadRecentReadings() {
        viewModelScope.launch {
            val farmer = farmerRepository.getFirstFarmer()
            if (farmer != null) {
                _recentReadings = soilRepository.getLastThreeReadings(farmer.id)
            }
        }
    }

    fun updateNitrogen(value: Float) {
        _uiState.value = _uiState.value.copy(nitrogen = value)
        updateSowingIndex()
    }

    fun updatePhosphorus(value: Float) {
        _uiState.value = _uiState.value.copy(phosphorus = value)
        updateSowingIndex()
    }

    fun updatePotassium(value: Float) {
        _uiState.value = _uiState.value.copy(potassium = value)
        updateSowingIndex()
    }

    fun updateMoisture(value: String) {
        _uiState.value = _uiState.value.copy(moisture = value)
        updateSowingIndex()
    }

    fun updateTemperature(value: Float) {
        _uiState.value = _uiState.value.copy(temperature = value)
        updateSowingIndex()
    }

    private fun updateSowingIndex() {
        viewModelScope.launch {
            val farmer = farmerRepository.getFirstFarmer()
            if (farmer != null) {
                val state = _uiState.value
                // For sowing index, we use moisture level (Dry=20, Moist=30, Wet=35, Waterlogged=40)
                val moistureValue = when (state.moisture) {
                    "Dry" -> 15f
                    "Moist" -> 25f
                    "Wet" -> 35f
                    "Waterlogged" -> 40f
                    else -> 25f
                }

                val index = SowingIndexCalculator.calculateIndex(
                    moisture = moistureValue,
                    temperature = state.temperature,
                    humidity = 65f, // Default humidity
                    crop = farmer.primaryCrop,
                    nitrogen = state.nitrogen,
                    phosphorus = state.phosphorus,
                    potassium = state.potassium
                )

                _uiState.value = state.copy(sowingIndex = index)
            }
        }
    }

    fun saveReading() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, isSaved = false)

        viewModelScope.launch {
            try {
                val farmer = farmerRepository.getFirstFarmer()
                if (farmer != null) {
                    val state = _uiState.value
                    val reading = SoilReading(
                        farmerId = farmer.id,
                        nitrogen = state.nitrogen,
                        phosphorus = state.phosphorus,
                        potassium = state.potassium,
                        moisture = state.moisture,
                        temperature = state.temperature
                    )
                    soilRepository.insertReading(reading)
                    _uiState.value = state.copy(
                        isLoading = false,
                        isSaved = true
                    )
                    loadRecentReadings()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Farmer not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error saving reading: ${e.message}"
                )
            }
        }
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}
