package com.inntemp.inningstempotracker.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inntemp.inningstempotracker.data.datastore.AppPreferences
import kotlinx.coroutines.launch

class OnboardingViewModel(private val preferences: AppPreferences) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            preferences.setOnboardingCompleted()
        }
    }
}
