package com.inntemp.inningstempotracker.ui.edit

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

data class EditOverFormState(
    val runs: String = "0",
    val wicket: Boolean = false,
    val phaseType: String = PhaseType.POWERPLAY,
    val note: String = "",
    val runsError: String? = null
)

data class EditInningUiState(
    val selectedOverId: Long? = null,
    val form: EditOverFormState = EditOverFormState(),
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val isAddFormVisible: Boolean = false,
    val addForm: EditOverFormState = EditOverFormState()
)

class EditInningViewModel(
    private val repository: InningsRepository,
    val matchId: Long,
    initialOverId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditInningUiState(selectedOverId = initialOverId.takeIf { it > 0 }))
    val uiState: StateFlow<EditInningUiState> = _uiState.asStateFlow()

    val matchDetail: StateFlow<MatchDetail?> = repository.getMatchDetail(matchId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectOver(over: Over) {
        _uiState.value = _uiState.value.copy(
            selectedOverId = over.id,
            form = EditOverFormState(
                runs = over.runs.toString(),
                wicket = over.wicket,
                phaseType = over.phaseType,
                note = over.note
            )
        )
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

    fun saveChanges() {
        val form = _uiState.value.form
        val overId = _uiState.value.selectedOverId ?: return
        val runs = form.runs.toIntOrNull()
        if (runs == null || runs < 0 || runs > 200) {
            _uiState.value = _uiState.value.copy(form = form.copy(runsError = "Enter a valid run count (0–200)"))
            return
        }

        val over = matchDetail.value?.overs?.find { it.id == overId } ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            repository.updateOver(
                over.copy(runs = runs, wicket = form.wicket, phaseType = form.phaseType, note = form.note)
            )
            _uiState.value = _uiState.value.copy(isSaving = false, savedSuccessfully = true)
        }
    }

    fun deleteOver(over: Over) {
        viewModelScope.launch {
            repository.deleteOver(over)
        }
    }

    fun showAddForm() {
        _uiState.value = _uiState.value.copy(isAddFormVisible = true, addForm = EditOverFormState())
    }

    fun dismissAddForm() {
        _uiState.value = _uiState.value.copy(isAddFormVisible = false, addForm = EditOverFormState())
    }

    fun onAddRunsChange(runs: String) {
        _uiState.value = _uiState.value.copy(addForm = _uiState.value.addForm.copy(runs = runs, runsError = null))
    }

    fun onAddWicketChange(wicket: Boolean) {
        _uiState.value = _uiState.value.copy(addForm = _uiState.value.addForm.copy(wicket = wicket))
    }

    fun onAddPhaseChange(phase: String) {
        _uiState.value = _uiState.value.copy(addForm = _uiState.value.addForm.copy(phaseType = phase))
    }

    fun onAddNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(addForm = _uiState.value.addForm.copy(note = note.take(100)))
    }

    fun saveNewOver() {
        val form = _uiState.value.addForm
        val runs = form.runs.toIntOrNull()
        if (runs == null || runs < 0 || runs > 200) {
            _uiState.value = _uiState.value.copy(addForm = form.copy(runsError = "Enter a valid run count (0–200)"))
            return
        }
        viewModelScope.launch {
            val currentOvers = matchDetail.value?.overs ?: emptyList()
            repository.saveOver(
                matchId = matchId,
                overNumber = currentOvers.size + 1,
                runs = runs,
                wicket = form.wicket,
                phaseType = form.phaseType,
                note = form.note
            )
            dismissAddForm()
        }
    }
}
