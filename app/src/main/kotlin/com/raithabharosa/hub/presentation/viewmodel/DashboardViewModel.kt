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
        fun text(en: String, kn: String) = if (language.startsWith("kn")) kn else en
        return when (cropType) {
            CropType.SUGARCANE -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "NPK 12:32:16 + Urea",
                quantity = "NPK ${(50 * area).toInt()} kg, Urea ${(25 * area).toInt()} kg",
                note = if (canGrow) text("Good window for planting sugarcane.", "ಹೆಬ್ಬತ್ತೆ ಬೆಳೆಗೆ ಇದನ್ನು ಬಿತ್ತಲು ಉತ್ತಮ ಸಮಯವಾಗಿದೆ.") else text("Wait for better moisture and temperature.", "ಉತ್ತಮ ತೇವಾಂಶ ಮತ್ತು ತಾಪಮಾನಕ್ಕಾಗಿ ಕಾಯಿರಿ.")
            )
            CropType.RAGI -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "DAP + FYM",
                quantity = "DAP ${(50 * area).toInt()} kg, FYM ${(2 * area).toInt()} tons",
                note = if (canGrow) text("Ragi can be sown now with balanced nutrition.", "ಸಮತೂಲ ಪೋಷಕಾಂಶಗಳೊಂದಿಗೆ ರಾಗಿ ಈಗ ಬಿತ್ತಬಹುದು.") else text("Improve soil moisture before sowing.", "ಬಿತ್ತುವ ಮೊದಲು ಮಣ್ಣಿನ ತೇವಾಂಶವನ್ನು ಹೆಚ್ಚಿಸಿ.")
            )
            CropType.PADDY -> CropAdvice(
                canGrowNow = canGrow,
                fertilizer = "Urea + SSP + MOP",
                quantity = "Urea ${(60 * area).toInt()} kg, SSP ${(100 * area).toInt()} kg, MOP ${(40 * area).toInt()} kg",
                note = if (canGrow) text("Paddy conditions look good for transplanting.", "ಬತ್ತೆ ನಾಟಿಗೆ ಪರಿಸ್ಥಿತಿ ಚೆನ್ನಾಗಿದೆ.") else text("Hold for better water and temperature.", "ಉತ್ತಮ ನೀರು ಮತ್ತು ತಾಪಮಾನಕ್ಕಾಗಿ ಕಾಯಿರಿ.")
            )
        }
    }
}
