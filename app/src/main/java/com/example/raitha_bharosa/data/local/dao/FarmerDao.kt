package com.example.raitha_bharosa.data.local.dao

import androidx.room.*
import com.example.raitha_bharosa.data.local.entity.FarmerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmerDao {
    @Query("SELECT * FROM farmers")
    fun getAllFarmers(): Flow<List<FarmerEntity>>

    @Query("SELECT * FROM farmers WHERE id = :id LIMIT 1")
    suspend fun getFarmerById(id: Long): FarmerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmer(farmer: FarmerEntity): Long

    @Update
    suspend fun updateFarmer(farmer: FarmerEntity)

    @Delete
    suspend fun deleteFarmer(farmer: FarmerEntity)
}
