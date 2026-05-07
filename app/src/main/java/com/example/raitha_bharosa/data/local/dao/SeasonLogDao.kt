package com.example.raitha_bharosa.data.local.dao

import androidx.room.*
import com.example.raitha_bharosa.data.local.entity.SeasonLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonLogDao {
    @Query("SELECT * FROM season_logs ORDER BY createdAt DESC")
    fun getAllSeasonLogs(): Flow<List<SeasonLogEntity>>

    @Query("SELECT * FROM season_logs WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getSeasonLogsByFarmerId(farmerId: Long): Flow<List<SeasonLogEntity>>

    @Query("SELECT * FROM season_logs WHERE id = :id LIMIT 1")
    suspend fun getSeasonLogById(id: Long): SeasonLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeasonLog(seasonLog: SeasonLogEntity): Long

    @Update
    suspend fun updateSeasonLog(seasonLog: SeasonLogEntity)

    @Delete
    suspend fun deleteSeasonLog(seasonLog: SeasonLogEntity)
}
