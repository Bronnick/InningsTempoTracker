package com.inntemp.inningstempotracker.ui.detail

import android.graphics.Color as AndroidColor
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.inntemp.inningstempotracker.R
import com.inntemp.inningstempotracker.data.model.MatchDetail
import com.inntemp.inningstempotracker.ui.navigation.Screen
import com.inntemp.inningstempotracker.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InningsDetailScreen(navController: NavController, matchId: Long) {
    val viewModel: InningsDetailViewModel = koinViewModel(parameters = { parametersOf(matchId) })
    val matchDetail by viewModel.matchDetail.collectAsState(initial = null)
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        TopAppBar(
            title = { Text(matchDetail?.name ?: "", style = typo.h3, color = colors.textPrimary) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back), tint = colors.textPrimary)
                }
            },
            actions = {
                IconButton(onClick = {
                    val firstOverId = matchDetail?.overs?.firstOrNull()?.id ?: 0L
                    navController.navigate(Screen.EditInning.createRoute(matchId, firstOverId))
                }) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit), tint = colors.primaryAccent)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.card)
        )

        when {
            matchDetail == null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.primaryAccent)
            }
            matchDetail?.overs.isNullOrEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.detail_no_data), style = typo.body, color = colors.textSecondary)
            }
            else -> InningsDetailContent(matchDetail = matchDetail!!, navController = navController)
        }
    }
}

@Composable
private fun InningsDetailContent(matchDetail: MatchDetail, navController: NavController) {
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimens.md),
        verticalArrangement = Arrangement.spacedBy(dimens.md)
    ) {
        // Stats row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimens.sm)) {
            StatCard(label = stringResource(R.string.detail_total_runs), value = matchDetail.totalRuns.toString(), modifier = Modifier.weight(1f))
            StatCard(label = stringResource(R.string.detail_wickets), value = matchDetail.totalWickets.toString(), modifier = Modifier.weight(1f))
            StatCard(label = stringResource(R.string.detail_run_rate), value = "%.2f".format(matchDetail.runRate), modifier = Modifier.weight(1f))
        }

        // Phase breakdown
        Text(text = stringResource(R.string.detail_phase_breakdown), style = typo.h3, color = colors.textPrimary)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimens.sm)) {
            StatCard(label = stringResource(R.string.phase_powerplay), value = matchDetail.powerplayRuns.toString(), modifier = Modifier.weight(1f))
            StatCard(label = stringResource(R.string.phase_middle), value = matchDetail.middleRuns.toString(), modifier = Modifier.weight(1f))
            StatCard(label = stringResource(R.string.phase_death), value = matchDetail.deathRuns.toString(), modifier = Modifier.weight(1f))
        }

        // Most impactful over
        matchDetail.highestScoringOver?.let { over ->
            Text(text = stringResource(R.string.detail_key_over), style = typo.h3, color = colors.textPrimary)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(shapes.md))
                    .clip(RoundedCornerShape(shapes.md))
                    .background(colors.card)
                    .padding(dimens.md)
            ) {
                Text(
                    text = stringResource(R.string.detail_key_over_desc, over.overNumber, over.runs, over.phaseType),
                    style = typo.body,
                    color = colors.textPrimary
                )
            }
        }

        // Tempo graph
        Text(text = stringResource(R.string.detail_tempo_graph), style = typo.h3, color = colors.textPrimary)
        TempoBarChart(matchDetail = matchDetail)

        // Compare button
        Button(
            onClick = { navController.navigate(Screen.Analytics.route) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colors.primaryAccent),
            shape = RoundedCornerShape(shapes.md)
        ) {
            Text(text = stringResource(R.string.detail_compare), color = androidx.compose.ui.graphics.Color.White)
        }

        Spacer(modifier = Modifier.height(dimens.md))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes

    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(shapes.md))
            .clip(RoundedCornerShape(shapes.md))
            .background(colors.card)
            .padding(dimens.md),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, style = typo.h2, color = colors.primaryAccent)
            Spacer(modifier = Modifier.height(dimens.xs))
            Text(text = label, style = typo.caption, color = colors.textSecondary)
        }
    }
}

@Composable
fun TempoBarChart(matchDetail: MatchDetail) {
    val accentColor = LocalAppTheme.colors.primaryAccent
    val accentColorInt = AndroidColor.rgb(
        (accentColor.red * 255).toInt(),
        (accentColor.green * 255).toInt(),
        (accentColor.blue * 255).toInt()
    )
    val dimens = LocalAppTheme.dimens

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(LocalAppTheme.shapes.md))
            .background(LocalAppTheme.colors.card)
            .padding(dimens.xs)
    ) {
        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    setTouchEnabled(false)
                    axisRight.isEnabled = false
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    xAxis.granularity = 1f
                    axisLeft.setDrawGridLines(true)
                    axisLeft.axisMinimum = 0f
                    setFitBars(true)
                }
            },
            update = { chart ->
                val entries = matchDetail.overs.mapIndexed { i, over ->
                    BarEntry(i.toFloat(), over.runs.toFloat())
                }
                val labels = matchDetail.overs.map { "O${it.overNumber}" }
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                val dataSet = BarDataSet(entries, "").apply {
                    color = accentColorInt
                    setDrawValues(false)
                }
                chart.data = BarData(dataSet)
                chart.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
