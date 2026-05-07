package com.example.raitha_bharosa.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "soil_readings",
    foreignKeys = [
        ForeignKey(
            entity = FarmerEntity::class,
            parentColumns = ["id"],
            childColumns = ["farmerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SoilReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val farmerId: Int,
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val moisture: String,
    val temperature: Float,
    val timestamp: Long
)
