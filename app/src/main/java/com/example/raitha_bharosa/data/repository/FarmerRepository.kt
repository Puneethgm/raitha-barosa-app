package com.example.raitha_bharosa.data.repository

import com.example.raitha_bharosa.data.local.dao.FarmerDao
import com.example.raitha_bharosa.data.local.dao.SeasonLogDao
import com.example.raitha_bharosa.data.local.entity.FarmerEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FarmerRepository @Inject constructor(
    private val farmerDao: FarmerDao,
    private val seasonLogDao: SeasonLogDao
) {
    
    fun getAllFarmers(): Flow<List<FarmerEntity>> {
        return farmerDao.getAllFarmers()
    }
    
    suspend fun getFarmerById(id: Long): FarmerEntity? {
        return farmerDao.getFarmerById(id)
    }
    
    suspend fun insertFarmer(farmer: FarmerEntity): Long {
        return farmerDao.insertFarmer(farmer)
    }
    
    suspend fun updateFarmer(farmer: FarmerEntity) {
        farmerDao.updateFarmer(farmer)
    }
    
    suspend fun deleteFarmer(farmer: FarmerEntity) {
        farmerDao.deleteFarmer(farmer)
    }
}
