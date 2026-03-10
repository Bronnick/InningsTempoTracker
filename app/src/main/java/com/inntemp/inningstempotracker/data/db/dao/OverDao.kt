package com.inntemp.inningstempotracker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.inntemp.inningstempotracker.data.db.entities.OverEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OverDao {

    @Query("SELECT * FROM overs WHERE matchId = :matchId ORDER BY overNumber ASC")
    fun getOversForMatch(matchId: Long): Flow<List<OverEntity>>

    @Query("SELECT * FROM overs WHERE matchId = :matchId ORDER BY overNumber ASC")
    suspend fun getOversForMatchOnce(matchId: Long): List<OverEntity>

    @Query("SELECT * FROM overs WHERE id = :overId")
    suspend fun getOverById(overId: Long): OverEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOver(over: OverEntity): Long

    @Update
    suspend fun updateOver(over: OverEntity)

    @Delete
    suspend fun deleteOver(over: OverEntity)

    @Query("DELETE FROM overs WHERE matchId = :matchId")
    suspend fun deleteOversForMatch(matchId: Long)

    @Query("SELECT * FROM overs ORDER BY matchId, overNumber ASC")
    suspend fun getAllOvers(): List<OverEntity>
}
