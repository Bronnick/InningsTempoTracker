package com.inntemp.inningstempotracker.ui.over

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.inntemp.inningstempotracker.R
import com.inntemp.inningstempotracker.data.model.Over
import com.inntemp.inningstempotracker.data.model.PhaseType
import com.inntemp.inningstempotracker.ui.navigation.Screen
import com.inntemp.inningstempotracker.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverInputScreen(navController: NavController, matchId: Long) {
    val viewModel: OverInputViewModel = koinViewModel(parameters = { parametersOf(matchId) })
    val uiState by viewModel.uiState.collectAsState()
    val matchDetail by viewModel.matchDetail.collectAsState()
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var overToDelete by remember { mutableStateOf<Over?>(null) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text(matchDetail?.name ?: "", style = typo.h3, color = colors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back), tint = colors.textPrimary)
                    }
                },
                actions = {
                    if ((matchDetail?.overs?.size ?: 0) > 0) {
                        TextButton(onClick = {
                            navController.navigate(Screen.InningsDetail.createRoute(matchId)) {
                                popUpTo(Screen.OverInput.createRoute(matchId)) { inclusive = true }
                            }
                        }) {
                            Text(stringResource(R.string.action_done), color = colors.primaryAccent, style = typo.body)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.card)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddForm() },
                containerColor = colors.primaryAccent
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.over_add), tint = Color.White)
            }
        }
    ) { innerPadding ->
        val overs = matchDetail?.overs ?: emptyList()

        if (overs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.over_empty), style = typo.body, color = colors.textSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(dimens.md),
                verticalArrangement = Arrangement.spacedBy(dimens.sm)
            ) {
                items(overs) { over ->
                    OverCard(
                        over = over,
                        onEdit = { viewModel.showEditForm(over) },
                        onDelete = { overToDelete = over }
                    )
                }
            }
        }
    }

    if (uiState.form.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissForm() },
            sheetState = bottomSheetState,
            containerColor = colors.card
        ) {
            OverForm(
                formState = uiState.form,
                onRunsChange = viewModel::onRunsChange,
                onWicketChange = viewModel::onWicketChange,
                onPhaseChange = viewModel::onPhaseChange,
                onNoteChange = viewModel::onNoteChange,
                onSave = { viewModel.saveOver() },
                onDismiss = { viewModel.dismissForm() }
            )
        }
    }

    overToDelete?.let { over ->
        AlertDialog(
            onDismissRequest = { overToDelete = null },
            title = { Text(stringResource(R.string.dialog_delete_over_title)) },
            text = { Text(stringResource(R.string.dialog_delete_over_message, over.overNumber)) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteOver(over); overToDelete = null }) {
                    Text(stringResource(R.string.action_delete), color = LocalAppTheme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { overToDelete = null }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
private fun OverCard(over: Over, onEdit: () -> Unit, onDelete: () -> Unit) {
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(shapes.md))
            .clip(RoundedCornerShape(shapes.md))
            .background(colors.card)
            .padding(dimens.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(R.string.over_label, over.overNumber), style = typo.h3, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(dimens.xs))
            Row(horizontalArrangement = Arrangement.spacedBy(dimens.sm)) {
                Text(text = stringResource(R.string.match_card_runs, over.runs), style = typo.body, color = colors.textPrimary)
                if (over.wicket) Text(text = stringResource(R.string.over_wicket_label), style = typo.body, color = colors.error)
                Text(text = over.phaseType, style = typo.caption, color = colors.primaryAccent)
            }
            if (over.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(dimens.xs))
                Text(text = over.note, style = typo.caption, color = colors.textSecondary, maxLines = 1)
            }
        }
        Row {
            IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit), tint = colors.iconActive) }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete), tint = colors.error) }
        }
    }
}

@Composable
private fun OverForm(
    formState: OverFormState,
    onRunsChange: (String) -> Unit,
    onWicketChange: (Boolean) -> Unit,
    onPhaseChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes

    Column(
        modifier = Modifier.fillMaxWidth().padding(dimens.md),
        verticalArrangement = Arrangement.spacedBy(dimens.md)
    ) {
        Text(
            text = if (formState.editingOverId != null) stringResource(R.string.over_edit_title) else stringResource(R.string.over_add_title),
            style = typo.h3,
            color = colors.textPrimary
        )

        OutlinedTextField(
            value = formState.runs,
            onValueChange = onRunsChange,
            label = { Text(stringResource(R.string.over_runs_label)) },
            isError = formState.runsError != null,
            supportingText = formState.runsError?.let { { Text(it, color = colors.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primaryAccent,
                unfocusedBorderColor = colors.border,
                focusedContainerColor = colors.inputBackground,
                unfocusedContainerColor = colors.inputBackground
            ),
            shape = RoundedCornerShape(shapes.sm)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.over_wicket_label), style = typo.body, color = colors.textPrimary)
            Switch(
                checked = formState.wicket,
                onCheckedChange = onWicketChange,
                colors = SwitchDefaults.colors(checkedThumbColor = colors.primaryAccent, checkedTrackColor = colors.secondaryAccent)
            )
        }

        Text(text = stringResource(R.string.over_phase_label), style = typo.body, color = colors.textPrimary)
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.sm)) {
            PhaseType.all.forEach { phase ->
                FilterChip(
                    selected = formState.phaseType == phase,
                    onClick = { onPhaseChange(phase) },
                    label = { Text(phase, style = typo.caption) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primaryAccent,
                        selectedLabelColor = Color.White,
                        containerColor = colors.inputBackground,
                        labelColor = colors.textPrimary
                    )
                )
            }
        }

        OutlinedTextField(
            value = formState.note,
            onValueChange = onNoteChange,
            label = { Text(stringResource(R.string.over_note_label)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primaryAccent,
                unfocusedBorderColor = colors.border,
                focusedContainerColor = colors.inputBackground,
                unfocusedContainerColor = colors.inputBackground
            ),
            shape = RoundedCornerShape(shapes.sm)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimens.sm)) {
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.action_cancel), color = colors.textSecondary)
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primaryAccent),
                shape = RoundedCornerShape(shapes.md)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                Text(text = stringResource(R.string.action_save), color = Color.White, modifier = Modifier.padding(start = dimens.xs))
            }
        }

        Spacer(modifier = Modifier.height(dimens.md))
    }
}
