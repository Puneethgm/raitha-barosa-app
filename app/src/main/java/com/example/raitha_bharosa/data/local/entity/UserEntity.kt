package com.example.raitha_bharosa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val hashedPassword: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false
)
