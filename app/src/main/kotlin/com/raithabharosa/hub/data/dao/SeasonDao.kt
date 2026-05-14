package com.raithabharosa.hub.data.dao
import androidx.room.*
import com.raithabharosa.hub.data.model.SeasonRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: SeasonRecord): Long
    @Query("SELECT * FROM season_records WHERE id = :id")
    suspend fun getSeason(id: Int): SeasonRecord?
    @Query("SELECT * FROM season_records WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getSeasonsByFarmer(farmerId: Int): Flow<List<SeasonRecord>>
    @Query("SELECT * FROM season_records WHERE farmerId = :farmerId ORDER BY createdAt DESC LIMIT 1")
    fun getLatestSeason(farmerId: Int): Flow<SeasonRecord?>
    @Update
    suspend fun updateSeason(season: SeasonRecord)
    @Delete
    suspend fun deleteSeason(season: SeasonRecord)
}
