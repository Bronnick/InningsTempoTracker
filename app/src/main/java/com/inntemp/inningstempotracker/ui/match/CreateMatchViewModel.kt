package com.inntemp.inningstempotracker.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inntemp.inningstempotracker.data.repository.InningsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateMatchUiState(
    val name: String = "",
    val format: String = "T20",
    val date: String = "",
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val dateError: String? = null,
    val createdMatchId: Long? = null
)

class CreateMatchViewModel(private val repository: InningsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMatchUiState())
    val uiState: StateFlow<CreateMatchUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name.take(100), nameError = null)
    }

    fun onFormatChange(format: String) {
        _uiState.value = _uiState.value.copy(format = format)
    }

    fun onDateChange(date: String) {
        _uiState.value = _uiState.value.copy(date = date, dateError = null)
    }

    fun submit() {
        val state = _uiState.value
        val nameError = if (state.name.isBlank()) "Name is required" else null
        val dateError = if (state.date.isBlank()) "Date is required" else null

        if (nameError != null || dateError != null) {
            _uiState.value = state.copy(nameError = nameError, dateError = dateError)
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            val matchId = repository.createMatch(state.name.trim(), state.format, state.date)
            _uiState.value = _uiState.value.copy(isLoading = false, createdMatchId = matchId)
        }
    }
}
