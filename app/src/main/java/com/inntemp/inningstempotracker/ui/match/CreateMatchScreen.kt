package com.inntemp.inningstempotracker.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.inntemp.inningstempotracker.R
import com.inntemp.inningstempotracker.data.model.MatchFormat
import com.inntemp.inningstempotracker.ui.navigation.Screen
import com.inntemp.inningstempotracker.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMatchScreen(navController: NavController) {
    val viewModel: CreateMatchViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes

    var showDatePicker by remember { mutableStateOf(false) }
    var showFormatDropdown by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= System.currentTimeMillis()
        }
    )
    val dateDisplayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    LaunchedEffect(uiState.createdMatchId) {
        uiState.createdMatchId?.let { matchId ->
            navController.navigate(Screen.OverInput.createRoute(matchId)) {
                popUpTo(Screen.CreateMatch.route) { inclusive = true }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.create_match_title), style = typo.h3, color = colors.textPrimary) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back), tint = colors.textPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.card)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(dimens.md),
            verticalArrangement = Arrangement.spacedBy(dimens.md)
        ) {
            Text(text = stringResource(R.string.create_match_name_label), style = typo.body, color = colors.textPrimary)
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.create_match_name_hint)) },
                isError = uiState.nameError != null,
                supportingText = uiState.nameError?.let { { Text(it, color = colors.error) } },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primaryAccent,
                    unfocusedBorderColor = colors.border,
                    focusedContainerColor = colors.inputBackground,
                    unfocusedContainerColor = colors.inputBackground
                ),
                shape = RoundedCornerShape(shapes.sm)
            )

            Text(text = stringResource(R.string.create_match_format_label), style = typo.body, color = colors.textPrimary)
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(shapes.sm))
                        .background(colors.inputBackground)
                        .border(width = 1.dp, color = colors.border, shape = RoundedCornerShape(shapes.sm))
                        .clickable { showFormatDropdown = true }
                        .padding(dimens.md)
                ) {
                    Text(text = uiState.format, style = typo.body, color = colors.textPrimary)
                }
                DropdownMenu(expanded = showFormatDropdown, onDismissRequest = { showFormatDropdown = false }) {
                    MatchFormat.all.forEach { fmt ->
                        DropdownMenuItem(
                            text = { Text(fmt) },
                            onClick = { viewModel.onFormatChange(fmt); showFormatDropdown = false }
                        )
                    }
                }
            }

            Text(text = stringResource(R.string.create_match_date_label), style = typo.body, color = colors.textPrimary)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(shapes.sm))
                    .background(colors.inputBackground)
                    .border(
                        width = if (uiState.dateError != null) 2.dp else 1.dp,
                        color = if (uiState.dateError != null) colors.error else colors.border,
                        shape = RoundedCornerShape(shapes.sm)
                    )
                    .clickable { showDatePicker = true }
                    .padding(dimens.md)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = uiState.date.ifBlank { stringResource(R.string.create_match_date_hint) },
                        style = typo.body,
                        color = if (uiState.date.isBlank()) colors.textSecondary else colors.textPrimary
                    )
                    Icon(Icons.Filled.DateRange, contentDescription = null, tint = colors.iconInactive)
                }
            }
            uiState.dateError?.let { Text(it, style = typo.caption, color = colors.error) }

            Spacer(modifier = Modifier.height(dimens.sm))

            Button(
                onClick = viewModel::submit,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primaryAccent),
                shape = RoundedCornerShape(shapes.md)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.padding(dimens.xs))
                } else {
                    Text(text = stringResource(R.string.create_match_submit), style = typo.body)
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.onDateChange(dateDisplayFormat.format(Date(millis)))
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.action_confirm)) }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.action_cancel)) } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
