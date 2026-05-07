package com.example.raitha_bharosa.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
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
    ]
)
data class SeasonLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val farmerId: Int,
    val date: String,
    val crop: String,
    val sowingIndex: Int,
    val actionTaken: String,
    val notes: String
)
