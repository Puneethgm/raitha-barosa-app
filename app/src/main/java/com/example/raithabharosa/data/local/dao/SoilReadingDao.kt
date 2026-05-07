package com.example.raitha_bharosa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.raitha_bharosa.data.local.entity.SoilReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoilReadingDao {
    @Insert
    suspend fun insertReading(reading: SoilReadingEntity): Long

    @Query("SELECT * FROM soil_readings WHERE farmerId = :farmerId ORDER BY timestamp DESC")
    fun getReadingsByFarmerId(farmerId: Int): Flow<List<SoilReadingEntity>>

    @Query("SELECT * FROM soil_readings WHERE farmerId = :farmerId ORDER BY timestamp DESC LIMIT 3")
    fun getLastThreeReadings(farmerId: Int): Flow<List<SoilReadingEntity>>

    @Query("DELETE FROM soil_readings WHERE farmerId = :farmerId")
    suspend fun deleteReadingsByFarmerId(farmerId: Int)

    @Query("DELETE FROM soil_readings")
    suspend fun deleteAllReadings()
}
