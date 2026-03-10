package com.inntemp.inningstempotracker.ui.over

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inntemp.inningstempotracker.data.model.MatchDetail
import com.inntemp.inningstempotracker.data.model.Over
import com.inntemp.inningstempotracker.data.model.PhaseType
import com.inntemp.inningstempotracker.data.repository.InningsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OverFormState(
    val runs: String = "0",
    val wicket: Boolean = false,
    val phaseType: String = PhaseType.POWERPLAY,
    val note: String = "",
    val runsError: String? = null,
    val isVisible: Boolean = false,
    val editingOverId: Long? = null
)

data class OverInputUiState(
    val isLoading: Boolean = true,
    val matchDetail: MatchDetail? = null,
    val form: OverFormState = OverFormState(),
    val error: String? = null
)

class OverInputViewModel(
    private val repository: InningsRepository,
    private val matchId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverInputUiState())
    val uiState: StateFlow<OverInputUiState> = _uiState.asStateFlow()

    val matchDetail: StateFlow<MatchDetail?> = repository.getMatchDetail(matchId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun showAddForm() {
        val nextOverNumber = (matchDetail.value?.overs?.size ?: 0) + 1
        _uiState.value = _uiState.value.copy(
            form = OverFormState(
                isVisible = true,
                phaseType = PhaseType.POWERPLAY
            )
        )
    }

    fun showEditForm(over: Over) {
        _uiState.value = _uiState.value.copy(
            form = OverFormState(
                runs = over.runs.toString(),
                wicket = over.wicket,
                phaseType = over.phaseType,
                note = over.note,
                isVisible = true,
                editingOverId = over.id
            )
        )
    }

    fun dismissForm() {
        _uiState.value = _uiState.value.copy(form = OverFormState())
    }

    fun onRunsChange(runs: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(runs = runs, runsError = null))
    }

    fun onWicketChange(wicket: Boolean) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(wicket = wicket))
    }

    fun onPhaseChange(phase: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(phaseType = phase))
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(form = _uiState.value.form.copy(note = note.take(100)))
    }

    fun saveOver() {
        val form = _uiState.value.form
        val runs = form.runs.toIntOrNull()
        if (runs == null || runs < 0 || runs > 200) {
            _uiState.value = _uiState.value.copy(form = form.copy(runsError = "Enter a valid run count (0–200)"))
            return
        }

        viewModelScope.launch {
            val currentOvers = matchDetail.value?.overs ?: emptyList()
            if (form.editingOverId != null) {
                repository.updateOver(
                    Over(id = form.editingOverId, matchId = matchId, overNumber = currentOvers.find { it.id == form.editingOverId }?.overNumber ?: 0, runs = runs, wicket = form.wicket, phaseType = form.phaseType, note = form.note)
                )
            } else {
                repository.saveOver(
                    matchId = matchId,
                    overNumber = currentOvers.size + 1,
                    runs = runs,
                    wicket = form.wicket,
                    phaseType = form.phaseType,
                    note = form.note
                )
            }
            dismissForm()
        }
    }

    fun deleteOver(over: Over) {
        viewModelScope.launch {
            repository.deleteOver(over)
        }
    }
}
