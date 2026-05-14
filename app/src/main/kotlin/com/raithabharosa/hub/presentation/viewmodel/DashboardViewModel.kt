package com.raithabharosa.hub.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.data.model.*
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.engine.DataGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val farmerProfile: FarmerProfile? = null,
    val sowingIndex: Float = 0f,
    val sowingStatus: SowingStatus = SowingStatus.RED,
    val recommendation: String = "",
    val currentSoilData: SoilData? = null,
    val currentWeatherData: WeatherData? = null,
    val errorMessage: String? = null
)

class DashboardViewModel(
    private val repository: FarmerRepository,
    private val dataGenerator: DataGenerator
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { loadDashboardData() }

    private fun loadDashboardData() {
        viewModelScope.launch {
            try {
                repository.getLatestFarmer().collect { farmer ->
                    if (farmer != null) {
                        _uiState.update { it.copy(farmerProfile = farmer) }
                        val soilData = dataGenerator.generateRealisticSoilData()
                        val weatherData = dataGenerator.generateRealisticWeatherData()
                        val cropType = CropType.valueOf(farmer.crop)
                        val result = repository.calculateAndStoreSowingIndex(1, soilData, weatherData, cropType)
                        _uiState.update {
                            it.copy(
                                currentSoilData = soilData,
                                currentWeatherData = weatherData,
                                sowingIndex = result.index,
                                sowingStatus = result.status,
                                recommendation = result.recommendation,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }

    fun refreshData() {
        _uiState.update { it.copy(isLoading = true) }
        loadDashboardData()
    }
}
