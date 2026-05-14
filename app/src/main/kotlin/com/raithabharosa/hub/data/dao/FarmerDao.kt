package com.raithabharosa.hub.data.dao
import androidx.room.*
import com.raithabharosa.hub.data.model.FarmerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmer(farmer: FarmerProfile): Long
    @Query("SELECT * FROM farmer_profiles WHERE id = :id")
    suspend fun getFarmer(id: Int): FarmerProfile?
    @Query("SELECT * FROM farmer_profiles")
    fun getAllFarmers(): Flow<List<FarmerProfile>>
    @Query("SELECT * FROM farmer_profiles ORDER BY lastUpdated DESC LIMIT 1")
    fun getLatestFarmer(): Flow<FarmerProfile?>
    @Update
    suspend fun updateFarmer(farmer: FarmerProfile)
    @Delete
    suspend fun deleteFarmer(farmer: FarmerProfile)
}
