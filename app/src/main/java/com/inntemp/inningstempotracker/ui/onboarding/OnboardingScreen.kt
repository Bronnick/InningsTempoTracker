package com.inntemp.inningstempotracker.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.inntemp.inningstempotracker.R
import com.inntemp.inningstempotracker.ui.navigation.Screen
import com.inntemp.inningstempotracker.ui.theme.LocalAppTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private data class OnboardingPage(val titleRes: Int, val descRes: Int)

private val pages = listOf(
    OnboardingPage(R.string.onboarding_1_title, R.string.onboarding_1_desc),
    OnboardingPage(R.string.onboarding_2_title, R.string.onboarding_2_desc),
    OnboardingPage(R.string.onboarding_3_title, R.string.onboarding_3_desc),
)

@Composable
fun OnboardingScreen(navController: NavController) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    fun finish() {
        viewModel.completeOnboarding()
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Onboarding.route) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(end = dimens.md, top = dimens.md), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = { finish() }) {
                Text(text = stringResource(R.string.action_skip), color = colors.textSecondary, style = typo.body)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            AnimatedVisibility(visible = true, enter = fadeIn()) {
                OnboardingPageContent(page = pages[page])
            }
        }

        // Dots
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = dimens.sm),
            horizontalArrangement = Arrangement.Center
        ) {
            pages.indices.forEach { i ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (i == pagerState.currentPage) 10.dp else 7.dp)
                        .clip(CircleShape)
                        .background(if (i == pagerState.currentPage) colors.primaryAccent else colors.border)
                )
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.md, vertical = dimens.md),
            horizontalArrangement = Arrangement.spacedBy(dimens.sm)
        ) {
            if (pagerState.currentPage > 0) {
                OutlinedButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.action_back), color = colors.primaryAccent)
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        finish()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primaryAccent),
                shape = RoundedCornerShape(LocalAppTheme.shapes.md)
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.lastIndex)
                        stringResource(R.string.action_get_started)
                    else
                        stringResource(R.string.action_next)
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimens.xl, vertical = dimens.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(page.titleRes),
            style = typo.h2,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimens.lg))
        Text(
            text = stringResource(page.descRes),
            style = typo.body,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}
