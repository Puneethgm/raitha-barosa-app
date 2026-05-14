package com.raithabharosa.hub.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmer_profiles")
data class FarmerProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, val crop: String, val location: String,
    val fieldAreaHectares: Double, val soilType: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)

enum class CropType { SUGARCANE, RAGI, PADDY }

data class SoilData(
    val nitrogen: Float, val phosphorus: Float, val potassium: Float,
    val moisture: Float, val temperature: Float, val pH: Float,
    val timestamp: Long = System.currentTimeMillis()
)

data class WeatherData(
    val temperature: Float, val humidity: Float, val rainfall: Float,
    val windSpeed: Float, val condition: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SowingIndexResult(
    val index: Float, val status: SowingStatus, val recommendation: String,
    val nextCheckIn: Long
)

enum class SowingStatus { GREEN, AMBER, RED }
