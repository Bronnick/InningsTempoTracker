package com.inntemp.inningstempotracker.ui.home

import androidx.lifecycle.ViewModel
import com.inntemp.inningstempotracker.data.model.MatchWithStats
import com.inntemp.inningstempotracker.data.repository.InningsRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel(repository: InningsRepository) : ViewModel() {
    val recentMatches: Flow<List<MatchWithStats>> = repository.getAllMatchesWithStats()
}
