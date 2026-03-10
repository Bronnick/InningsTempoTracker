package com.inntemp.inningstempotracker.data.repository

import com.inntemp.inningstempotracker.data.model.MatchDetail
import com.inntemp.inningstempotracker.data.model.MatchWithStats
import com.inntemp.inningstempotracker.data.model.Over
import kotlinx.coroutines.flow.Flow

interface InningsRepository {
    fun getAllMatchesWithStats(): Flow<List<MatchWithStats>>
    fun getMatchDetail(matchId: Long): Flow<MatchDetail?>
    suspend fun createMatch(name: String, format: String, date: String): Long
    suspend fun deleteMatch(matchId: Long)
    suspend fun deleteAllMatches()
    suspend fun saveOver(matchId: Long, overNumber: Int, runs: Int, wicket: Boolean, phaseType: String, note: String): Long
    suspend fun updateOver(over: Over)
    suspend fun deleteOver(over: Over)
    suspend fun exportToJson(): String
    suspend fun importFromJson(json: String)
}
