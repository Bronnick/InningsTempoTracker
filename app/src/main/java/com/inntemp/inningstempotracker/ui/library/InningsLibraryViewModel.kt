package com.inntemp.inningstempotracker.ui.library

import androidx.lifecycle.ViewModel
import com.inntemp.inningstempotracker.data.model.MatchWithStats
import com.inntemp.inningstempotracker.data.repository.InningsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class InningsLibraryViewModel(repository: InningsRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: Flow<String> = _searchQuery

    val matches: Flow<List<MatchWithStats>> = repository.getAllMatchesWithStats()
        .combine(_searchQuery) { list, query ->
            if (query.isBlank()) list
            else list.filter { it.name.contains(query, ignoreCase = true) || it.format.contains(query, ignoreCase = true) }
        }

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }
}
