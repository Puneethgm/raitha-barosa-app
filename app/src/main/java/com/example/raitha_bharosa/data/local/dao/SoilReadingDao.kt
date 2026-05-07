package com.example.raitha_bharosa.data.local.dao

import androidx.room.*
import com.example.raitha_bharosa.data.local.entity.SoilReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoilReadingDao {
    @Query("SELECT * FROM soil_readings ORDER BY timestamp DESC")
    fun getAllSoilReadings(): Flow<List<SoilReadingEntity>>

    @Query("SELECT * FROM soil_readings WHERE farmerId = :farmerId ORDER BY timestamp DESC")
    fun getSoilReadingsByFarmerId(farmerId: Long): Flow<List<SoilReadingEntity>>

    @Query("SELECT * FROM soil_readings WHERE id = :id LIMIT 1")
    suspend fun getSoilReadingById(id: Long): SoilReadingEntity?

    @Query("SELECT * FROM soil_readings ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLatestSoilReadings(limit: Int): List<SoilReadingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoilReading(soilReading: SoilReadingEntity): Long

    @Update
    suspend fun updateSoilReading(soilReading: SoilReadingEntity)

    @Delete
    suspend fun deleteSoilReading(soilReading: SoilReadingEntity)
}
