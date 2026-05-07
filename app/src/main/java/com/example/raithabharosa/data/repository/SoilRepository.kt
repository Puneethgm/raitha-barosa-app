package com.example.raitha_bharosa.data.repository

import com.example.raitha_bharosa.data.local.dao.SoilReadingDao
import com.example.raitha_bharosa.data.local.entity.SoilReadingEntity
import com.example.raitha_bharosa.domain.model.SoilReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SoilRepository @Inject constructor(
    private val soilReadingDao: SoilReadingDao
) {

    suspend fun insertReading(reading: SoilReading): Long {
        return soilReadingDao.insertReading(reading.toEntity())
    }

    fun getReadingsByFarmerId(farmerId: Int): Flow<List<SoilReading>> {
        return soilReadingDao.getReadingsByFarmerId(farmerId).map { readings ->
            readings.map { it.toDomain() }
        }
    }

    fun getLastThreeReadings(farmerId: Int): Flow<List<SoilReading>> {
        return soilReadingDao.getLastThreeReadings(farmerId).map { readings ->
            readings.map { it.toDomain() }
        }
    }

    suspend fun deleteReadingsByFarmerId(farmerId: Int) {
        soilReadingDao.deleteReadingsByFarmerId(farmerId)
    }

    suspend fun deleteAllReadings() {
        soilReadingDao.deleteAllReadings()
    }

    private fun SoilReading.toEntity() = SoilReadingEntity(
        id = id,
        farmerId = farmerId,
        nitrogen = nitrogen,
        phosphorus = phosphorus,
        potassium = potassium,
        moisture = moisture,
        temperature = temperature,
        timestamp = timestamp
    )

    private fun SoilReadingEntity.toDomain() = SoilReading(
        id = id,
        farmerId = farmerId,
        nitrogen = nitrogen,
        phosphorus = phosphorus,
        potassium = potassium,
        moisture = moisture,
        temperature = temperature,
        timestamp = timestamp
    )
}
