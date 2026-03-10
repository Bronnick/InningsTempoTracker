package com.inntemp.inningstempotracker.ui.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inntemp.inningstempotracker.BuildConfig
import com.inntemp.inningstempotracker.data.datastore.AppPreferences
import com.inntemp.inningstempotracker.data.repository.InningsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val exportedJson: String? = null,
    val snackbarMessage: String? = null,
    val showClearLibraryDialog: Boolean = false,
    val showResetSettingsDialog: Boolean = false
)

class SettingsViewModel(
    private val repository: InningsRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val currentTheme: StateFlow<String> = preferences.themeSelection
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "light")

    val appVersion: String = BuildConfig.VERSION_NAME

    fun setTheme(theme: String) {
        viewModelScope.launch { preferences.setTheme(theme) }
    }

    fun exportData(onReady: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            try {
                val json = repository.exportToJson()
                onReady(json)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(snackbarMessage = "Export failed")
            } finally {
                _uiState.value = _uiState.value.copy(isExporting = false)
            }
        }
    }

    fun importData(json: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true)
            try {
                repository.importFromJson(json)
                _uiState.value = _uiState.value.copy(snackbarMessage = "Data imported successfully")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(snackbarMessage = "Import failed: invalid file")
            } finally {
                _uiState.value = _uiState.value.copy(isImporting = false)
            }
        }
    }

    fun showClearLibraryDialog() { _uiState.value = _uiState.value.copy(showClearLibraryDialog = true) }
    fun dismissClearLibraryDialog() { _uiState.value = _uiState.value.copy(showClearLibraryDialog = false) }

    fun clearLibrary() {
        viewModelScope.launch {
            repository.deleteAllMatches()
            _uiState.value = _uiState.value.copy(showClearLibraryDialog = false, snackbarMessage = "Library cleared")
        }
    }

    fun showResetSettingsDialog() { _uiState.value = _uiState.value.copy(showResetSettingsDialog = true) }
    fun dismissResetSettingsDialog() { _uiState.value = _uiState.value.copy(showResetSettingsDialog = false) }

    fun resetSettings() {
        viewModelScope.launch {
            preferences.resetAll()
            _uiState.value = _uiState.value.copy(showResetSettingsDialog = false, snackbarMessage = "Settings reset")
        }
    }

    fun shareApp(context: Context) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out Innings Tempo Tracker on Google Play!")
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    fun clearSnackbar() { _uiState.value = _uiState.value.copy(snackbarMessage = null) }
}
