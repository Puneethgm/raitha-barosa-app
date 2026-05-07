package com.example.raitha_bharosa.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    ],
    indices = [Index(value = ["farmerId"])]
)
data class SoilReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val farmerId: Long,
    val ph: Double,
    val nitrogen: Double,
    val phosphorus: Double,
    val potassium: Double,
    val moisture: Double,
    val temperature: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = ""
)
