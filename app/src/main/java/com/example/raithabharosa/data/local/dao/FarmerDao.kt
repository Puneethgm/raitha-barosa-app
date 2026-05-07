package com.example.raitha_bharosa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.raitha_bharosa.data.local.entity.FarmerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmerDao {
    @Insert
    suspend fun insertFarmer(farmer: FarmerEntity): Long

    @Query("SELECT * FROM farmers WHERE id = :id")
    suspend fun getFarmerById(id: Int): FarmerEntity?

    @Query("SELECT * FROM farmers LIMIT 1")
    suspend fun getFirstFarmer(): FarmerEntity?

    @Query("SELECT * FROM farmers")
    fun getAllFarmers(): Flow<List<FarmerEntity>>

    @Update
    suspend fun updateFarmer(farmer: FarmerEntity)

    @Query("DELETE FROM farmers WHERE id = :id")
    suspend fun deleteFarmer(id: Int)

    @Query("DELETE FROM farmers")
    suspend fun deleteAllFarmers()
}
