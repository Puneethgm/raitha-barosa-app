package com.example.raitha_bharosa.data.repository

import com.example.raitha_bharosa.data.local.dao.SoilReadingDao
import com.example.raitha_bharosa.data.local.entity.SoilReadingEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoilRepository @Inject constructor(
    private val soilReadingDao: SoilReadingDao
) {
    
    fun getAllSoilReadings(): Flow<List<SoilReadingEntity>> {
        return soilReadingDao.getAllSoilReadings()
    }
    
    fun getSoilReadingsByFarmerId(farmerId: Long): Flow<List<SoilReadingEntity>> {
        return soilReadingDao.getSoilReadingsByFarmerId(farmerId)
    }
    
    suspend fun getSoilReadingById(id: Long): SoilReadingEntity? {
        return soilReadingDao.getSoilReadingById(id)
    }
    
    suspend fun getLatestSoilReadings(limit: Int): List<SoilReadingEntity> {
        return soilReadingDao.getLatestSoilReadings(limit)
    }
    
    suspend fun insertSoilReading(soilReading: SoilReadingEntity): Long {
        return soilReadingDao.insertSoilReading(soilReading)
    }
    
    suspend fun updateSoilReading(soilReading: SoilReadingEntity) {
        soilReadingDao.updateSoilReading(soilReading)
    }
    
    suspend fun deleteSoilReading(soilReading: SoilReadingEntity) {
        soilReadingDao.deleteSoilReading(soilReading)
    }
}
