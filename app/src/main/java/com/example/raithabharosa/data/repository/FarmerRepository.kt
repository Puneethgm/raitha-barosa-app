package com.example.raitha_bharosa.data.repository

import com.example.raitha_bharosa.data.local.dao.FarmerDao
import com.example.raitha_bharosa.data.local.entity.FarmerEntity
import com.example.raitha_bharosa.domain.model.Farmer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FarmerRepository @Inject constructor(
    private val farmerDao: FarmerDao
) {

    suspend fun insertFarmer(farmer: Farmer): Long {
        return farmerDao.insertFarmer(farmer.toEntity())
    }

    suspend fun getFarmerById(id: Int): Farmer? {
        return farmerDao.getFarmerById(id)?.toDomain()
    }

    suspend fun getFirstFarmer(): Farmer? {
        return farmerDao.getFirstFarmer()?.toDomain()
    }

    fun getAllFarmers(): Flow<List<Farmer>> {
        return farmerDao.getAllFarmers().map { farmers ->
            farmers.map { it.toDomain() }
        }
    }

    suspend fun updateFarmer(farmer: Farmer) {
        farmerDao.updateFarmer(farmer.toEntity())
    }

    suspend fun deleteFarmer(id: Int) {
        farmerDao.deleteFarmer(id)
    }

    suspend fun deleteAllFarmers() {
        farmerDao.deleteAllFarmers()
    }

    private fun Farmer.toEntity() = FarmerEntity(
        id = id,
        name = name,
        village = village,
        primaryCrop = primaryCrop,
        language = language,
        createdAt = createdAt
    )

    private fun FarmerEntity.toDomain() = Farmer(
        id = id,
        name = name,
        village = village,
        primaryCrop = primaryCrop,
        language = language,
        createdAt = createdAt
    )
}
