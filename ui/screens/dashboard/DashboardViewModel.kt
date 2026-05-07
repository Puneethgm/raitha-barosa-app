package com.example.raitha_bharosa.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raitha_bharosa.data.repository.FarmerRepository
import com.example.raitha_bharosa.data.repository.SoilRepository
import com.example.raitha_bharosa.data.repository.WeatherRepository
import com.example.raitha_bharosa.domain.model.Farmer
import com.example.raitha_bharosa.util.SowingIndexCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DashboardUiState(
    val farmer: Farmer? = null,
    val sowingIndex: Int = 50,
    val temperature: Float = 25f,
    val humidity: Float = 65f,
    val moisture: Float = 25f,
    val todayAction: String = "Get Ready",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val farmerRepository: FarmerRepository,
    private val weatherRepository: WeatherRepository,
    private val soilRepository: SoilRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val farmer = farmerRepository.getFirstFarmer()
                if (farmer != null) {
                    val temp = weatherRepository.getSimulatedTemperature()
                    val humidity = weatherRepository.getSimulatedHumidity()
                    val moisture = weatherRepository.getSimulatedMoisture()

                    val sowingIndex = SowingIndexCalculator.calculateIndex(
                        moisture = moisture,
                        temperature = temp,
                        humidity = humidity,
                        crop = farmer.primaryCrop
                    )

                    val action = when {
                        sowingIndex <= 40 -> "Wait"
                        sowingIndex <= 70 -> "Get Ready"
                        else -> "Sow Now"
                    }

                    _uiState.value = _uiState.value.copy(
                        farmer = farmer,
                        sowingIndex = sowingIndex,
                        temperature = temp,
                        humidity = humidity,
                        moisture = moisture,
                        todayAction = action,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No farmer found. Please complete onboarding."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading dashboard: ${e.message}"
                )
            }
        }
    }

    fun refresh() {
        loadDashboard()
    }
}
