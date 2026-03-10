package com.inntemp.inningstempotracker.ui.preloader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inntemp.inningstempotracker.data.datastore.AppPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PreloaderViewModel(private val preferences: AppPreferences) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Ready(val onboardingCompleted: Boolean) : State()
        object Error : State()
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        initialize()
    }

    fun initialize() {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                delay(1500)
                val onboardingCompleted = preferences.onboardingCompleted.first()
                _state.value = State.Ready(onboardingCompleted)
            } catch (e: Exception) {
                _state.value = State.Error
            }
        }
    }
}
