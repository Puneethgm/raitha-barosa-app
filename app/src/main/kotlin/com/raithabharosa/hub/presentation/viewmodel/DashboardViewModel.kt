package com.raithabharosa.hub.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.data.model.*
import com.raithabharosa.hub.data.repository.FarmerRepository
import com.raithabharosa.hub.domain.engine.DataGenerator
import kotlinx.coroutines.flow.*
import com.raithabharosa.hub.data.repository.WeatherRepository
import com.raithabharosa.hub.data.repository.ScheduledActionRepository
import com.raithabharosa.hub.data.storage.SessionManager
import com.raithabharosa.hub.data.model.ScheduledAction
import com.raithabharosa.hub.data.model.WeatherData
import kotlinx.coroutines.flow.first
import com.raithabharosa.hub.data.location.FusedLocationProvider
import android.location.Location
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val farmerProfile: FarmerProfile? = null,
    val selectedCropType: CropType = CropType.RAGI,
    val sowingIndex: Float = 0f,
    val sowingStatus: SowingStatus = SowingStatus.RED,
    val recommendation: String = "",
    val currentSoilData: SoilData? = null,
    val currentWeatherData: WeatherData? = null,
    val cropAdvice: CropAdvice? = null,
    val scheduledActions: List<ScheduledAction> = emptyList(),
    val errorMessage: String? = null
)

data class CropAdvice(
    val canGrowNow: Boolean,
    val fertilizer: String,
    val quantity: String,
    val note: String
)

class DashboardViewModel(
    private val repository: FarmerRepository,
    private val dataGenerator: DataGenerator,
    private val weatherRepo: WeatherRepository? = null,
    private val scheduledRepo: ScheduledActionRepository? = null,
    private val sessionManager: SessionManager? = null,
    private val fusedLocationProvider: FusedLocationProvider? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { loadDashboardData() }

    fun refreshData(cropType: CropType? = null) {
        _uiState.update { it.copy(isLoading = true) }
        loadDashboardData(cropType)
    }

    fun selectCropType(cropType: CropType) {
        _uiState.update { it.copy(selectedCropType = cropType, isLoading = true) }
        loadDashboardData(cropType)
    }

    private fun loadDashboardData(cropTypeOverride: CropType? = null) {
        viewModelScope.launch {
            try {
                val farmer = repository.getLatestFarmer().first()
                if (farmer != null) {
                    val cropType = cropTypeOverride ?: runCatching { CropType.valueOf(farmer.crop.uppercase()) }.getOrDefault(CropType.RAGI)
                    _uiState.update { it.copy(farmerProfile = farmer, selectedCropType = cropType) }
                    val soilData = dataGenerator.generateRealisticSoilData()
                    var weatherData = dataGenerator.generateRealisticWeatherData()
                    try {
                        if (weatherRepo != null) {
                            var lat = 12.9716
                            var lon = 77.5946
                            try {
                                val loc: Location? = fusedLocationProvider?.getLastLocation()
                                if (loc != null) { lat = loc.latitude; lon = loc.longitude }
                            } catch (_: Throwable) {}
                            val wf = weatherRepo.get7DayForecast(lat, lon)
                            if (wf.isSuccess) {
                                val first = wf.getOrNull()?.daily?.firstOrNull()
                                first?.let {
                                    weatherData = WeatherData(
                                        temperature = it.temp.day.toFloat(),
                                        humidity = 0f,
                                        rainfall = 0f,
                                        windSpeed = 0f,
                                        condition = it.weather.firstOrNull()?.description ?: "",
                                        timestamp = it.dt * 1000
                                    )
                                }
                            }
                        }
                    } catch (_: Throwable) {}
                    val result = repository.calculateAndStoreSowingIndex(1, soilData, weatherData, cropType)
                    val language = sessionManager?.languageFlow?.first() ?: "en"
                    _uiState.update {
                        it.copy(
                            currentSoilData = soilData,
                            currentWeatherData = weatherData,
                            cropAdvice = buildCropAdvice(cropType, result.status, farmer.fieldAreaHectares, language),
                            scheduledActions = fetchScheduledActions(),
                            sowingIndex = result.index,
                            sowingStatus = result.status,
                            recommendation = result.recommendation,
                            isLoading = false
                        )
                    }
                } else {
                    val cropType = cropTypeOverride ?: _uiState.value.selectedCropType
                    val soilData = dataGenerator.generateRealisticSoilData()
                    var weatherData = dataGenerator.generateRealisticWeatherData()
                    try {
                        if (weatherRepo != null) {
                            var lat = 12.9716
                            var lon = 77.5946
                            try {
                                val loc: Location? = fusedLocationProvider?.getLastLocation()
                                if (loc != null) { lat = loc.latitude; lon = loc.longitude }
                            } catch (_: Throwable) {}
                            val wf = weatherRepo.get7DayForecast(lat, lon)
                            if (wf.isSuccess) {
                                val first = wf.getOrNull()?.daily?.firstOrNull()
                                first?.let {
                                    weatherData = WeatherData(
                                        temperature = it.temp.day.toFloat(),
                                        humidity = 0f,
                                        rainfall = 0f,
                                        windSpeed = 0f,
                                        condition = it.weather.firstOrNull()?.description ?: "",
                                        timestamp = it.dt * 1000
                                    )
                                }
                            }
                        }
                    } catch (_: Throwable) {}

                    val result = repository.calculateAndStoreSowingIndex(1, soilData, weatherData, cropType)
                    val language = sessionManager?.languageFlow?.first() ?: "en"
                    _uiState.update {
                        it.copy(
                            farmerProfile = null,
                            selectedCropType = cropType,
                            currentSoilData = soilData,
                            currentWeatherData = weatherData,
                            cropAdvice = buildCropAdvice(cropType, result.status, 1.0, language),
                            scheduledActions = fetchScheduledActions(),
                            sowingIndex = result.index,
                            sowingStatus = result.status,
                            recommendation = result.recommendation,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }

    private suspend fun fetchScheduledActions(): List<ScheduledAction> {
        return try {
            val uid = sessionManager?.currentUserIdFlow?.first()
            if (uid != null && scheduledRepo != null) {
                scheduledRepo.listForUser(uid).getOrDefault(emptyList())
            } else emptyList()
        } catch (t: Throwable) { emptyList() }
    }

    private fun buildCropAdvice(cropType: CropType, status: SowingStatus, fieldAreaHectares: Double, language: String): CropAdvice {
        val canGrow = status == SowingStatus.GREEN
        val area = if (fieldAreaHectares > 0) fieldAreaHectares else 1.0
        return when (cropType) {
            CropType.SUGARCANE -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "NPK 12:32:16 + Urea",
                quantity = "NPK ${(50 * area).toInt()} kg, Urea ${(25 * area).toInt()} kg",
                note = if (canGrow) "Good window for planting sugarcane." else "Wait for better moisture and temperature."
            )
            CropType.RAGI -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "DAP + FYM",
                quantity = "DAP ${(50 * area).toInt()} kg, FYM ${(2 * area).toInt()} tons",
                note = if (canGrow) "Ragi can be sown now with balanced nutrition." else "Improve soil moisture before sowing."
            )
            CropType.PADDY -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "Urea + SSP + MOP",
                quantity = "Urea ${(60 * area).toInt()} kg, SSP ${(100 * area).toInt()} kg, MOP ${(40 * area).toInt()} kg",
                note = if (canGrow) "Paddy conditions look good for transplanting." else "Hold for better water and temperature."
            )
            CropType.COTTON -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "DAP + NPK",
                quantity = "DAP ${(50 * area).toInt()} kg, NPK ${(30 * area).toInt()} kg",
                note = if (canGrow) "Cotton sowing window is open. Apply nutrients now." else "Wait for warmer conditions."
            )
            CropType.CORN -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "Urea + DAP",
                quantity = "DAP ${(50 * area).toInt()} kg, Urea ${(30 * area).toInt()} kg",
                note = if (canGrow) "Corn is ready for sowing. Good moisture levels." else "Improve soil conditions before sowing."
            )
            CropType.WHEAT -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "DAP + MOP",
                quantity = "DAP ${(50 * area).toInt()} kg, MOP ${(30 * area).toInt()} kg",
                note = if (canGrow) "Optimal conditions for wheat sowing." else "Wait for cooler season."
            )
            CropType.SOYBEAN -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "NPK 0:20:20",
                quantity = "NPK ${(40 * area).toInt()} kg",
                note = if (canGrow) "Soybean sowing conditions are favorable." else "Monitor moisture levels."
            )
            CropType.GROUNDNUT -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "DAP + Gypsum",
                quantity = "DAP ${(40 * area).toInt()} kg, Gypsum ${(20 * area).toInt()} kg",
                note = if (canGrow) "Groundnut planting time is now." else "Wait for better soil conditions."
            )
            CropType.SUNFLOWER -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "NPK 15:15:15",
                quantity = "NPK ${(40 * area).toInt()} kg",
                note = if (canGrow) "Sunflower sowing is recommended." else "Improve moisture levels."
            )
            CropType.CHILI -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "NPK 10:52:10",
                quantity = "NPK ${(50 * area).toInt()} kg",
                note = if (canGrow) "Chili transplanting window is open." else "Wait for warmer weather."
            )
            CropType.TOMATO -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "DAP + Potassium",
                quantity = "DAP ${(40 * area).toInt()} kg, K ${(20 * area).toInt()} kg",
                note = if (canGrow) "Tomato transplanting is favorable." else "Wait for stable temperatures."
            )
            CropType.ONION -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "NPK 10:26:26",
                quantity = "NPK ${(40 * area).toInt()} kg",
                note = if (canGrow) "Onion planting conditions are ideal." else "Monitor soil moisture."
            )
        }
    }
}
