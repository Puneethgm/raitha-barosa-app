package com.example.raitha_bharosa.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raitha_bharosa.data.repository.FarmerRepository
import com.example.raitha_bharosa.data.repository.WeatherRepository
import com.example.raitha_bharosa.domain.model.DayForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KrishiCalendarUiState(
    val forecasts: List<DayForecast> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class KrishiCalendarViewModel @Inject constructor(
    private val farmerRepository: FarmerRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(KrishiCalendarUiState())
    val uiState: StateFlow<KrishiCalendarUiState> = _uiState

    init {
        loadForecasts()
    }

    private fun loadForecasts() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val farmer = farmerRepository.getFirstFarmer()
                val forecasts = if (farmer != null) {
                    weatherRepository.get7DayForecast(farmer.village)
                } else {
                    weatherRepository.getSimulatedWeather()
                }
                _uiState.value = _uiState.value.copy(
                    forecasts = forecasts,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading forecasts: ${e.message}"
                )
            }
        }
    }

    fun refresh() {
        loadForecasts()
    }
}
