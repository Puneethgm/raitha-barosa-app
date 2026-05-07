package com.example.raitha_bharosa.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raitha_bharosa.data.repository.AuthRepository
import com.example.raitha_bharosa.data.local.dao.FarmerDao
import com.example.raitha_bharosa.data.local.dao.SeasonLogDao
import com.example.raitha_bharosa.data.local.dao.SoilReadingDao
import com.example.raitha_bharosa.data.repository.FarmerRepository
import com.example.raitha_bharosa.domain.model.Farmer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val farmer: Farmer? = null,
    val selectedLanguage: String = "en",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isClearDialogOpen: Boolean = false,
    val isEditMode: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val farmerRepository: FarmerRepository,
    private val authRepository: AuthRepository,
    private val farmerDao: FarmerDao,
    private val soilReadingDao: SoilReadingDao,
    private val seasonLogDao: SeasonLogDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val farmer = farmerRepository.getFirstFarmer()
                if (farmer != null) {
                    _uiState.value = _uiState.value.copy(
                        farmer = farmer,
                        selectedLanguage = farmer.language,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No farmer found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading settings: ${e.message}"
                )
            }
        }
    }

    fun updateLanguage(language: String) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)

        viewModelScope.launch {
            try {
                val farmer = _uiState.value.farmer
                if (farmer != null) {
                    val updated = farmer.copy(language = language)
                    farmerRepository.updateFarmer(updated)
                    _uiState.value = _uiState.value.copy(farmer = updated)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error updating language: ${e.message}"
                )
            }
        }
    }

    fun updateCrop(crop: String) {
        viewModelScope.launch {
            try {
                val farmer = _uiState.value.farmer
                if (farmer != null) {
                    val updated = farmer.copy(primaryCrop = crop)
                    farmerRepository.updateFarmer(updated)
                    _uiState.value = _uiState.value.copy(farmer = updated)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error updating crop: ${e.message}"
                )
            }
        }
    }

    fun updateVillage(village: String) {
        viewModelScope.launch {
            try {
                val farmer = _uiState.value.farmer
                if (farmer != null) {
                    val updated = farmer.copy(village = village)
                    farmerRepository.updateFarmer(updated)
                    _uiState.value = _uiState.value.copy(farmer = updated)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error updating village: ${e.message}"
                )
            }
        }
    }

    fun openClearDialog() {
        _uiState.value = _uiState.value.copy(isClearDialogOpen = true)
    }

    fun closeClearDialog() {
        _uiState.value = _uiState.value.copy(isClearDialogOpen = false)
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                farmerRepository.deleteAllFarmers()
                soilReadingDao.deleteAllReadings()
                seasonLogDao.deleteAllLogs()
                authRepository.logout()
                _uiState.value = _uiState.value.copy(
                    farmer = null,
                    isClearDialogOpen = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error clearing data: ${e.message}"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
