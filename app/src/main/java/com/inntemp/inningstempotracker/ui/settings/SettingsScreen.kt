package com.inntemp.inningstempotracker.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.inntemp.inningstempotracker.R
import com.inntemp.inningstempotracker.ui.theme.LocalAppTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val context = LocalContext.current
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes

    var showSnackbar by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { destUri ->
            viewModel.exportData { json ->
                context.contentResolver.openOutputStream(destUri)?.use { it.write(json.toByteArray()) }
                viewModel.clearSnackbar()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { srcUri ->
            context.contentResolver.openInputStream(srcUri)?.use { stream ->
                val json = stream.bufferedReader().readText()
                viewModel.importData(json)
            }
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        if (uiState.snackbarMessage != null) {
            showSnackbar = true
            delay(3000)
            showSnackbar = false
            viewModel.clearSnackbar()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.md)
        ) {
            Spacer(modifier = Modifier.height(dimens.lg))
            Text(text = stringResource(R.string.tab_settings), style = typo.h2, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(dimens.md))

            // Theme
            SettingsSectionHeader(stringResource(R.string.settings_section_appearance))
            SettingsCard(shapes.md) {
                Column {
                    Text(stringResource(R.string.settings_theme), style = typo.body, color = colors.textSecondary, modifier = Modifier.padding(bottom = dimens.sm))
                    listOf("light" to stringResource(R.string.theme_light), "dark" to stringResource(R.string.theme_dark)).forEach { (value, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.setTheme(value) }.padding(vertical = dimens.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentTheme == value,
                                onClick = { viewModel.setTheme(value) },
                                colors = RadioButtonDefaults.colors(selectedColor = colors.primaryAccent)
                            )
                            Text(label, style = typo.body, color = colors.textPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimens.md))
            SettingsSectionHeader(stringResource(R.string.settings_section_data))
            SettingsCard(shapes.md) {
                Column {
                    SettingsItem(label = stringResource(R.string.settings_export), onClick = {
                        exportLauncher.launch("innings_tempo_export.json")
                    })
                    HorizontalDivider(color = colors.border)
                    SettingsItem(label = stringResource(R.string.settings_import), onClick = {
                        importLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                    })
                    HorizontalDivider(color = colors.border)
                    SettingsItem(label = stringResource(R.string.settings_clear_library), labelColor = colors.error, onClick = {
                        viewModel.showClearLibraryDialog()
                    })
                }
            }

            Spacer(modifier = Modifier.height(dimens.md))
            SettingsSectionHeader(stringResource(R.string.settings_section_general))
            SettingsCard(shapes.md) {
                Column {
                    SettingsItem(label = stringResource(R.string.settings_rate_app), onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                        runCatching { context.startActivity(intent) }
                    })
                    HorizontalDivider(color = colors.border)
                    SettingsItem(label = stringResource(R.string.settings_share_app), onClick = {
                        viewModel.shareApp(context)
                    })
                    HorizontalDivider(color = colors.border)
                    SettingsItem(label = stringResource(R.string.settings_privacy_policy), onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/privacy"))
                        runCatching { context.startActivity(intent) }
                    })
                    HorizontalDivider(color = colors.border)
                    SettingsItem(label = stringResource(R.string.settings_reset), labelColor = colors.error, onClick = {
                        viewModel.showResetSettingsDialog()
                    })
                }
            }

            Spacer(modifier = Modifier.height(dimens.md))
            SettingsCard(shapes.md) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.settings_version), style = typo.body, color = colors.textSecondary)
                    Text(viewModel.appVersion, style = typo.body, color = colors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(dimens.xl))
        }

        if (showSnackbar && uiState.snackbarMessage != null) {
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(dimens.md)) {
                Snackbar { Text(uiState.snackbarMessage!!, style = typo.body) }
            }
        }
    }

    if (uiState.showClearLibraryDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissClearLibraryDialog,
            title = { Text(stringResource(R.string.dialog_clear_library_title)) },
            text = { Text(stringResource(R.string.dialog_clear_library_message)) },
            confirmButton = {
                TextButton(onClick = viewModel::clearLibrary) {
                    Text(stringResource(R.string.action_confirm), color = LocalAppTheme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissClearLibraryDialog) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }

    if (uiState.showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissResetSettingsDialog,
            title = { Text(stringResource(R.string.dialog_reset_settings_title)) },
            text = { Text(stringResource(R.string.dialog_reset_settings_message)) },
            confirmButton = {
                TextButton(onClick = viewModel::resetSettings) {
                    Text(stringResource(R.string.action_confirm), color = LocalAppTheme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissResetSettingsDialog) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = LocalAppTheme.typography.caption,
        color = LocalAppTheme.colors.textSecondary,
        modifier = Modifier.padding(vertical = LocalAppTheme.dimens.sm)
    )
}

@Composable
private fun SettingsCard(cornerRadius: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .background(LocalAppTheme.colors.card)
            .padding(LocalAppTheme.dimens.md)
    ) {
        content()
    }
}

@Composable
private fun SettingsItem(
    label: String,
    labelColor: androidx.compose.ui.graphics.Color = LocalAppTheme.colors.textPrimary,
    onClick: () -> Unit
) {
    val dimens = LocalAppTheme.dimens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimens.sm)
    ) {
        Text(text = label, style = LocalAppTheme.typography.body, color = labelColor)
    }
}
