package com.inntemp.inningstempotracker.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.inntemp.inningstempotracker.R
import com.inntemp.inningstempotracker.data.model.MatchWithStats
import com.inntemp.inningstempotracker.ui.navigation.Screen
import com.inntemp.inningstempotracker.ui.theme.LocalAppTheme
import com.inntemp.inningstempotracker.utils.DateUtils
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = koinViewModel()
    val matches by viewModel.recentMatches.collectAsState(initial = emptyList())
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens

    Scaffold(
        containerColor = colors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateMatch.route) },
                containerColor = colors.primaryAccent
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.home_start_new_match), tint = androidx.compose.ui.graphics.Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimens.md)
        ) {
            Spacer(modifier = Modifier.height(dimens.lg))
            Text(text = stringResource(R.string.app_name), style = typo.h2, color = colors.primaryAccent)
            Spacer(modifier = Modifier.height(dimens.sm))
            Text(text = stringResource(R.string.home_recent_innings), style = typo.h3, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(dimens.md))

            if (matches.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimens.sm),
                    contentPadding = PaddingValues(bottom = dimens.xl)
                ) {
                    items(matches.take(10)) { match ->
                        MatchCard(match = match, onClick = {
                            navController.navigate(Screen.InningsDetail.createRoute(match.id))
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.home_empty_title), style = typo.h3, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(dimens.sm))
            Text(text = stringResource(R.string.home_empty_desc), style = typo.body, color = colors.textSecondary)
        }
    }
}

@Composable
fun MatchCard(match: MatchWithStats, onClick: () -> Unit) {
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(shapes.md))
            .clip(RoundedCornerShape(shapes.md))
            .background(colors.card)
            .clickable(onClick = onClick)
            .padding(dimens.md)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = match.name,
                    style = typo.h3,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(text = match.format, style = typo.caption, color = colors.primaryAccent)
            }
            Spacer(modifier = Modifier.height(dimens.xs))
            Row(horizontalArrangement = Arrangement.spacedBy(dimens.md)) {
                Text(text = stringResource(R.string.match_card_runs, match.totalRuns), style = typo.body, color = colors.textPrimary)
                Text(text = stringResource(R.string.match_card_wickets, match.totalWickets), style = typo.body, color = colors.textSecondary)
                Text(text = stringResource(R.string.match_card_overs, match.overCount), style = typo.body, color = colors.textSecondary)
            }
            Spacer(modifier = Modifier.height(dimens.xs))
            Text(text = DateUtils.formatForDisplay(match.date), style = typo.caption, color = colors.textSecondary)
        }
    }
}
