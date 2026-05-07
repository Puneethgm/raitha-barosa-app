package com.example.raitha_bharosa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmers")
data class FarmerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val village: String,
    val primaryCrop: String,
    val language: String,
    val createdAt: Long
)
