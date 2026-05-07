package com.example.raitha_bharosa.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raitha_bharosa.data.local.dao.SeasonLogDao
import com.example.raitha_bharosa.data.local.entity.SeasonLogEntity
import com.example.raitha_bharosa.data.repository.FarmerRepository
import com.example.raitha_bharosa.domain.model.SeasonLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HistoryUiState(
    val logs: List<SeasonLog> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isDialogOpen: Boolean = false,
    val newDate: String = "",
    val newAction: String = "",
    val newNotes: String = ""
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val farmerRepository: FarmerRepository,
    private val seasonLogDao: SeasonLogDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    private var _logs: Flow<List<SeasonLog>> = emptyFlow()
    val logs: Flow<List<SeasonLog>>
        get() = _logs

    init {
        loadLogs()
    }

    private fun loadLogs() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val farmer = farmerRepository.getFirstFarmer()
                if (farmer != null) {
                    _logs = seasonLogDao.getLogsByFarmerId(farmer.id).map { entities ->
                        entities.map { entity ->
                            SeasonLog(
                                id = entity.id,
                                farmerId = entity.farmerId,
                                date = entity.date,
                                crop = entity.crop,
                                sowingIndex = entity.sowingIndex,
                                actionTaken = entity.actionTaken,
                                notes = entity.notes
                            )
                        }
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No farmer found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading logs: ${e.message}"
                )
            }
        }
    }

    fun openDialog() {
        _uiState.value = _uiState.value.copy(
            isDialogOpen = true,
            newDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        )
    }

    fun closeDialog() {
        _uiState.value = _uiState.value.copy(
            isDialogOpen = false,
            newDate = "",
            newAction = "",
            newNotes = ""
        )
    }

    fun updateDate(date: String) {
        _uiState.value = _uiState.value.copy(newDate = date)
    }

    fun updateAction(action: String) {
        _uiState.value = _uiState.value.copy(newAction = action)
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(newNotes = notes)
    }

    fun addLog() {
        val state = _uiState.value

        viewModelScope.launch {
            try {
                val farmer = farmerRepository.getFirstFarmer()
                if (farmer != null) {
                    val logEntity = SeasonLogEntity(
                        farmerId = farmer.id,
                        date = state.newDate,
                        crop = farmer.primaryCrop,
                        sowingIndex = 50,
                        actionTaken = state.newAction,
                        notes = state.newNotes
                    )
                    // Insert into the database
                    seasonLogDao.insertLog(logEntity)
                    loadLogs()
                    closeDialog()
                }
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    error = "Error adding log: ${e.message}"
                )
            }
        }
    }
}
