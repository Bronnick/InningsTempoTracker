package com.inntemp.inningstempotracker.ui.analytics

import androidx.lifecycle.ViewModel
import com.inntemp.inningstempotracker.data.model.MatchDetail
import com.inntemp.inningstempotracker.data.model.MatchWithStats
import com.inntemp.inningstempotracker.data.repository.InningsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

data class AnalyticsUiState(
    val selectedMatchIds: Set<Long> = emptySet()
)

class AnalyticsViewModel(private val repository: InningsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    val allMatches: Flow<List<MatchWithStats>> = repository.getAllMatchesWithStats()

    fun toggleMatch(matchId: Long) {
        val current = _uiState.value.selectedMatchIds
        _uiState.value = _uiState.value.copy(
            selectedMatchIds = if (matchId in current) current - matchId else current + matchId
        )
    }

    fun getMatchDetail(matchId: Long): Flow<MatchDetail?> = repository.getMatchDetail(matchId)
}
