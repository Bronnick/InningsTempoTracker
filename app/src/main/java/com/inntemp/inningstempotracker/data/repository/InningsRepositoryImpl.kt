package com.inntemp.inningstempotracker.data.repository

import com.inntemp.inningstempotracker.data.db.dao.MatchDao
import com.inntemp.inningstempotracker.data.db.dao.OverDao
import com.inntemp.inningstempotracker.data.db.entities.MatchEntity
import com.inntemp.inningstempotracker.data.db.entities.OverEntity
import com.inntemp.inningstempotracker.data.model.ExportData
import com.inntemp.inningstempotracker.data.model.ExportMatch
import com.inntemp.inningstempotracker.data.model.MatchDetail
import com.inntemp.inningstempotracker.data.model.MatchWithStats
import com.inntemp.inningstempotracker.data.model.Over
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class InningsRepositoryImpl(
    private val matchDao: MatchDao,
    private val overDao: OverDao
) : InningsRepository {

    override fun getAllMatchesWithStats(): Flow<List<MatchWithStats>> =
        matchDao.getAllMatchesWithOvers().map { list ->
            list.map { mwo ->
                MatchWithStats(
                    id = mwo.match.id,
                    name = mwo.match.name,
                    format = mwo.match.format,
                    date = mwo.match.date,
                    totalRuns = mwo.overs.sumOf { it.runs },
                    totalWickets = mwo.overs.count { it.wicket },
                    overCount = mwo.overs.size,
                    createdAt = mwo.match.createdAt
                )
            }
        }

    override fun getMatchDetail(matchId: Long): Flow<MatchDetail?> =
        matchDao.getMatchWithOvers(matchId).map { mwo ->
            mwo?.let {
                MatchDetail(
                    id = it.match.id,
                    name = it.match.name,
                    format = it.match.format,
                    date = it.match.date,
                    overs = it.overs.map { oe -> oe.toDomain() }
                )
            }
        }

    override suspend fun createMatch(name: String, format: String, date: String): Long =
        matchDao.insertMatch(MatchEntity(name = name, format = format, date = date))

    override suspend fun deleteMatch(matchId: Long) {
        matchDao.getMatchById(matchId)?.let { matchDao.deleteMatch(it) }
    }

    override suspend fun deleteAllMatches() = matchDao.deleteAllMatches()

    override suspend fun saveOver(
        matchId: Long, overNumber: Int, runs: Int, wicket: Boolean, phaseType: String, note: String
    ): Long = overDao.insertOver(
        OverEntity(matchId = matchId, overNumber = overNumber, runs = runs, wicket = wicket, phaseType = phaseType, note = note)
    )

    override suspend fun updateOver(over: Over) =
        overDao.updateOver(over.toEntity())

    override suspend fun deleteOver(over: Over) =
        overDao.deleteOver(over.toEntity())

    override suspend fun exportToJson(): String {
        val allMatchesWithOvers = matchDao.getAllMatchesWithOvers().first()
        val exportData = ExportData(
            matches = allMatchesWithOvers.map { mwo ->
                ExportMatch(
                    id = mwo.match.id,
                    name = mwo.match.name,
                    format = mwo.match.format,
                    date = mwo.match.date,
                    createdAt = mwo.match.createdAt,
                    overs = mwo.overs.map { it.toDomain() }
                )
            }
        )
        return Json.encodeToString(exportData)
    }

    override suspend fun importFromJson(json: String) {
        val exportData = Json.decodeFromString<ExportData>(json)
        exportData.matches.forEach { exportMatch ->
            val newMatchId = matchDao.insertMatch(
                MatchEntity(
                    name = exportMatch.name,
                    format = exportMatch.format,
                    date = exportMatch.date,
                    createdAt = exportMatch.createdAt
                )
            )
            exportMatch.overs.forEach { over ->
                overDao.insertOver(
                    OverEntity(
                        matchId = newMatchId,
                        overNumber = over.overNumber,
                        runs = over.runs,
                        wicket = over.wicket,
                        phaseType = over.phaseType,
                        note = over.note
                    )
                )
            }
        }
    }

    private fun OverEntity.toDomain() = Over(
        id = id, matchId = matchId, overNumber = overNumber,
        runs = runs, wicket = wicket, phaseType = phaseType, note = note
    )

    private fun Over.toEntity() = OverEntity(
        id = id, matchId = matchId, overNumber = overNumber,
        runs = runs, wicket = wicket, phaseType = phaseType, note = note
    )
}
