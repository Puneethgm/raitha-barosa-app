package com.example.raitha_bharosa.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "season_logs",
    foreignKeys = [
        ForeignKey(
            entity = FarmerEntity::class,
            parentColumns = ["id"],
            childColumns = ["farmerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["farmerId"])]
)
data class SeasonLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val farmerId: Long,
    val season: String,
    val cropType: String,
    val sowingDate: Long,
    val expectedHarvestDate: Long,
    val actualHarvestDate: Long? = null,
    val yield: Double? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
