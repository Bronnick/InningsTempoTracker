package com.inntemp.inningstempotracker.ui.preloader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.inntemp.inningstempotracker.R
import com.inntemp.inningstempotracker.ui.navigation.Screen
import com.inntemp.inningstempotracker.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PreloaderScreen(navController: NavController) {
    val viewModel: PreloaderViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens

    LaunchedEffect(state) {
        when (val s = state) {
            is PreloaderViewModel.State.Ready -> {
                val destination = if (s.onboardingCompleted) Screen.Home.route else Screen.Onboarding.route
                navController.navigate(destination) {
                    popUpTo(Screen.Preloader.route) { inclusive = true }
                }
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is PreloaderViewModel.State.Loading, is PreloaderViewModel.State.Ready -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = typo.h1,
                        color = colors.primaryAccent
                    )
                    Spacer(modifier = Modifier.height(dimens.xl))
                    CircularProgressIndicator(
                        color = colors.primaryAccent,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(dimens.md))
                    Text(
                        text = stringResource(R.string.preloader_initializing),
                        style = typo.body,
                        color = colors.textSecondary
                    )
                }
            }
            PreloaderViewModel.State.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.preloader_error),
                        style = typo.body,
                        color = colors.error
                    )
                    Spacer(modifier = Modifier.height(dimens.md))
                    Button(onClick = { viewModel.initialize() }) {
                        Text(text = stringResource(R.string.action_retry))
                    }
                }
            }
        }
    }
}
