package com.inntemp.inningstempotracker.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.inntemp.inningstempotracker.data.db.entities.MatchEntity
import com.inntemp.inningstempotracker.data.db.entities.OverEntity

data class MatchWithOvers(
    @Embedded val match: MatchEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "matchId"
    )
    val overs: List<OverEntity>
)
