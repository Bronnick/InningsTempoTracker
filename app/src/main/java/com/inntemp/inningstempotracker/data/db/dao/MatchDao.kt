package com.inntemp.inningstempotracker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.inntemp.inningstempotracker.data.db.entities.MatchEntity
import com.inntemp.inningstempotracker.data.db.relations.MatchWithOvers
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Transaction
    @Query("SELECT * FROM matches ORDER BY createdAt DESC")
    fun getAllMatchesWithOvers(): Flow<List<MatchWithOvers>>

    @Transaction
    @Query("SELECT * FROM matches WHERE id = :matchId")
    fun getMatchWithOvers(matchId: Long): Flow<MatchWithOvers?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Delete
    suspend fun deleteMatch(match: MatchEntity)

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: Long): MatchEntity?
}
