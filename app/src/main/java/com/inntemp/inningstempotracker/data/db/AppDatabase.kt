package com.inntemp.inningstempotracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.inntemp.inningstempotracker.data.db.dao.MatchDao
import com.inntemp.inningstempotracker.data.db.dao.OverDao
import com.inntemp.inningstempotracker.data.db.entities.MatchEntity
import com.inntemp.inningstempotracker.data.db.entities.OverEntity

@Database(
    entities = [MatchEntity::class, OverEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
    abstract fun overDao(): OverDao
}
