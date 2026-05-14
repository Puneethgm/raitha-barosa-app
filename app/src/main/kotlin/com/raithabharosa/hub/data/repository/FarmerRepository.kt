package com.raithabharosa.hub.data.repository
import com.raithabharosa.hub.data.dao.*
import com.raithabharosa.hub.data.model.*
import com.raithabharosa.hub.domain.engine.SowingIndexEngine
import kotlinx.coroutines.flow.Flow

class FarmerRepository(
    private val farmerDao: FarmerDao,
    private val seasonDao: SeasonDao,
    private val soilDao: SoilDao,
    private val dailyActionDao: DailyActionDao,
    private val sowingIndexEngine: SowingIndexEngine
) {
    fun getAllFarmers(): Flow<List<FarmerProfile>> = farmerDao.getAllFarmers()
    fun getLatestFarmer(): Flow<FarmerProfile?> = farmerDao.getLatestFarmer()
    suspend fun createFarmer(farmer: FarmerProfile): Long = farmerDao.insertFarmer(farmer)
    suspend fun updateFarmer(farmer: FarmerProfile) = farmerDao.updateFarmer(farmer)
    suspend fun getFarmer(id: Int): FarmerProfile? = farmerDao.getFarmer(id)
    fun getSeasonsByFarmer(farmerId: Int): Flow<List<SeasonRecord>> = seasonDao.getSeasonsByFarmer(farmerId)
    fun getLatestSeason(farmerId: Int): Flow<SeasonRecord?> = seasonDao.getLatestSeason(farmerId)
    suspend fun createSeason(season: SeasonRecord): Long = seasonDao.insertSeason(season)
    suspend fun updateSeason(season: SeasonRecord) = seasonDao.updateSeason(season)
    fun getSoilRecords(seasonId: Int): Flow<List<SoilRecord>> = soilDao.getSoilRecords(seasonId)
    fun getLatestSoilRecord(seasonId: Int): Flow<SoilRecord?> = soilDao.getLatestSoilRecord(seasonId)
    suspend fun recordSoilData(soilRecord: SoilRecord): Long = soilDao.insertSoilRecord(soilRecord)
    fun getDailyActions(seasonId: Int): Flow<List<DailyAction>> = dailyActionDao.getActionsForSeason(seasonId)
    suspend fun updateDailyAction(action: DailyAction) = dailyActionDao.updateAction(action)
    suspend fun createDailyActions(actions: List<DailyAction>) {
        actions.forEach { dailyActionDao.insertAction(it) }
    }
    fun calculateAndStoreSowingIndex(seasonId: Int, soilData: SoilData, weatherData: WeatherData, cropType: CropType): SowingIndexResult {
        return sowingIndexEngine.calculateSowingIndex(soilData, weatherData, cropType)
    }
}
