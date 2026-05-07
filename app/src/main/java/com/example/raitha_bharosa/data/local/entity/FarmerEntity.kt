package com.example.raitha_bharosa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmers")
data class FarmerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val location: String,
    val farmSize: Double,
    val cropType: String,
    val createdAt: Long = System.currentTimeMillis()
)
