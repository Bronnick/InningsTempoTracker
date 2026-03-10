package com.inntemp.inningstempotracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val format: String,
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
)
