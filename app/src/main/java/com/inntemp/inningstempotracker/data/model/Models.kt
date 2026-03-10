package com.inntemp.inningstempotracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Over(
    val id: Long = 0,
    val matchId: Long,
    val overNumber: Int,
    val runs: Int,
    val wicket: Boolean,
    val phaseType: String,
    val note: String = ""
)

data class MatchWithStats(
    val id: Long,
    val name: String,
    val format: String,
    val date: String,
    val totalRuns: Int,
    val totalWickets: Int,
    val overCount: Int,
    val createdAt: Long
)

data class MatchDetail(
    val id: Long,
    val name: String,
    val format: String,
    val date: String,
    val overs: List<Over>
) {
    val totalRuns: Int get() = overs.sumOf { it.runs }
    val totalWickets: Int get() = overs.count { it.wicket }
    val runRate: Float get() = if (overs.isEmpty()) 0f else totalRuns.toFloat() / overs.size
    val highestScoringOver: Over? get() = overs.maxByOrNull { it.runs }
    val powerplayRuns: Int get() = overs.filter { it.phaseType == PhaseType.POWERPLAY }.sumOf { it.runs }
    val middleRuns: Int get() = overs.filter { it.phaseType == PhaseType.MIDDLE }.sumOf { it.runs }
    val deathRuns: Int get() = overs.filter { it.phaseType == PhaseType.DEATH }.sumOf { it.runs }
}

@Serializable
data class ExportData(
    val matches: List<ExportMatch>
)

@Serializable
data class ExportMatch(
    val id: Long,
    val name: String,
    val format: String,
    val date: String,
    val createdAt: Long,
    val overs: List<Over>
)

object PhaseType {
    const val POWERPLAY = "Powerplay"
    const val MIDDLE = "Middle"
    const val DEATH = "Death"
    val all = listOf(POWERPLAY, MIDDLE, DEATH)
}

object MatchFormat {
    const val T20 = "T20"
    const val ODI = "ODI"
    const val TEST = "Test"
    const val CUSTOM = "Custom"
    val all = listOf(T20, ODI, TEST, CUSTOM)
}
