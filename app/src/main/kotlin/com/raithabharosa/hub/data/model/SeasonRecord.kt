package com.raithabharosa.hub.data.model
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "season_records", foreignKeys = [
    ForeignKey(entity = FarmerProfile::class, parentColumns = ["id"],
    childColumns = ["farmerId"], onDelete = ForeignKey.CASCADE)
])
data class SeasonRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val farmerId: Int, val season: String, val cropType: String,
    val sowingDate: Long, val harvestDate: Long?,
    val yieldKgPerHectare: Float?,
    val totalNitrogenUsed: Float, val totalPhosphorusUsed: Float,
    val totalPotassiumUsed: Float, val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "soil_records", foreignKeys = [
    ForeignKey(entity = SeasonRecord::class, parentColumns = ["id"],
    childColumns = ["seasonId"], onDelete = ForeignKey.CASCADE)
])
data class SoilRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val seasonId: Int, val nitrogen: Float, val phosphorus: Float,
    val potassium: Float, val moisture: Float, val temperature: Float,
    val pH: Float, val recordedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_actions", foreignKeys = [
    ForeignKey(entity = SeasonRecord::class, parentColumns = ["id"],
    childColumns = ["seasonId"], onDelete = ForeignKey.CASCADE)
])
data class DailyAction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val seasonId: Int, val dayNumber: Int, val action: String,
    val reasoning: String, val completed: Boolean = false,
    val completedAt: Long? = null, val createdAt: Long = System.currentTimeMillis()
)
