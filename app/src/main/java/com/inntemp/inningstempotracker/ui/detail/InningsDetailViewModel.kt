package com.inntemp.inningstempotracker.ui.detail

import androidx.lifecycle.ViewModel
import com.inntemp.inningstempotracker.data.model.MatchDetail
import com.inntemp.inningstempotracker.data.repository.InningsRepository
import kotlinx.coroutines.flow.Flow

class InningsDetailViewModel(
    repository: InningsRepository,
    val matchId: Long
) : ViewModel() {
    val matchDetail: Flow<MatchDetail?> = repository.getMatchDetail(matchId)
}
