package com.inntemp.inningstempotracker.ui.analytics

import android.graphics.Color as AndroidColor
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.inntemp.inningstempotracker.R
import com.inntemp.inningstempotracker.data.model.MatchDetail
import com.inntemp.inningstempotracker.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel

private val lineColors = listOf(
    0xFF2476D1.toInt(),
    0xFFE53935.toInt(),
    0xFF43A047.toInt(),
    0xFFFB8C00.toInt()
)

@Composable
fun AnalyticsScreen(navController: NavController) {
    val viewModel: AnalyticsViewModel = koinViewModel()
    val allMatches by viewModel.allMatches.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppTheme.colors
    val typo = LocalAppTheme.typography
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes

    val selectedDetails = uiState.selectedMatchIds.mapIndexed { i, id ->
        val detail by produceState<MatchDetail?>(null, id) {
            viewModel.getMatchDetail(id).collect { value = it }
        }
        detail
    }.filterNotNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = dimens.md)
    ) {
        Spacer(modifier = Modifier.height(dimens.lg))
        Text(text = stringResource(R.string.tab_analytics), style = typo.h2, color = colors.textPrimary)
        Spacer(modifier = Modifier.height(dimens.sm))
        Text(text = stringResource(R.string.analytics_select_hint), style = typo.body, color = colors.textSecondary)
        Spacer(modifier = Modifier.height(dimens.md))

        if (allMatches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.analytics_empty), style = typo.body, color = colors.textSecondary)
            }
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = dimens.xl),
            verticalArrangement = Arrangement.spacedBy(dimens.sm)
        ) {
            items(allMatches.take(4)) { match ->
                val isSelected = match.id in uiState.selectedMatchIds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(shapes.md))
                        .clip(RoundedCornerShape(shapes.md))
                        .background(colors.card)
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) colors.primaryAccent else androidx.compose.ui.graphics.Color.Transparent,
                            shape = RoundedCornerShape(shapes.md)
                        )
                        .clickable { viewModel.toggleMatch(match.id) }
                        .padding(dimens.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { viewModel.toggleMatch(match.id) },
                        colors = CheckboxDefaults.colors(checkedColor = colors.primaryAccent)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(match.name, style = typo.body, color = colors.textPrimary)
                        Text("${match.format} · ${match.totalRuns} runs", style = typo.caption, color = colors.textSecondary)
                    }
                }
            }

            if (selectedDetails.isNotEmpty()) {
                item { Spacer(modifier = Modifier.height(dimens.md)) }
                item {
                    Text(stringResource(R.string.analytics_tempo_chart), style = typo.h3, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(dimens.sm))
                    ComparisonLineChart(matchDetails = selectedDetails)
                }

                item { Spacer(modifier = Modifier.height(dimens.md)) }
                item {
                    Text(stringResource(R.string.analytics_averages), style = typo.h3, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(dimens.sm))
                }

                items(selectedDetails) { detail ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(shapes.md))
                            .clip(RoundedCornerShape(shapes.md))
                            .background(colors.card)
                            .padding(dimens.md),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(detail.name, style = typo.body, color = colors.textPrimary)
                        Text(
                            stringResource(R.string.analytics_rr_value, detail.runRate),
                            style = typo.body,
                            color = colors.primaryAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonLineChart(matchDetails: List<MatchDetail>) {
    val dimens = LocalAppTheme.dimens
    val shapes = LocalAppTheme.shapes
    val colors = LocalAppTheme.colors
    val cardColor = colors.card

    val textColor = colors.textSecondary
    val textColorInt = AndroidColor.rgb(
        (textColor.red * 255).toInt(),
        (textColor.green * 255).toInt(),
        (textColor.blue * 255).toInt()
    )
    val legendColor = colors.textPrimary
    val legendColorInt = AndroidColor.rgb(
        (legendColor.red * 255).toInt(),
        (legendColor.green * 255).toInt(),
        (legendColor.blue * 255).toInt()
    )
    val borderColor = colors.border
    val borderColorInt = AndroidColor.rgb(
        (borderColor.red * 255).toInt(),
        (borderColor.green * 255).toInt(),
        (borderColor.blue * 255).toInt()
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(shapes.md))
            .background(cardColor)
            .padding(dimens.xs)
    ) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false
                    setTouchEnabled(false)
                    setBackgroundColor(AndroidColor.TRANSPARENT)
                    axisRight.isEnabled = false
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    xAxis.granularity = 1f
                    axisLeft.setDrawGridLines(true)
                    axisLeft.axisMinimum = 0f
                }
            },
            update = { chart ->
                chart.xAxis.textColor = textColorInt
                chart.axisLeft.textColor = textColorInt
                chart.axisLeft.gridColor = borderColorInt
                chart.legend.textColor = legendColorInt

                val maxOvers = matchDetails.maxOfOrNull { it.overs.size } ?: 0
                val labels = (1..maxOvers).map { "O$it" }
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

                val dataSets = matchDetails.mapIndexed { idx, detail ->
                    val entries = detail.overs.mapIndexed { i, over -> Entry(i.toFloat(), over.runs.toFloat()) }
                    LineDataSet(entries, detail.name).apply {
                        color = lineColors[idx % lineColors.size]
                        setCircleColor(lineColors[idx % lineColors.size])
                        lineWidth = 2f
                        circleRadius = 3f
                        setDrawValues(false)
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                    }
                }
                chart.data = LineData(dataSets)
                chart.legend.isEnabled = matchDetails.size > 1
                chart.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
