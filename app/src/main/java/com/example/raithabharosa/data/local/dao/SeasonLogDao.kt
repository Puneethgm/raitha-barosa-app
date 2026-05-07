package com.example.raitha_bharosa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.raitha_bharosa.data.local.entity.SeasonLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonLogDao {
    @Insert
    suspend fun insertLog(log: SeasonLogEntity): Long

    @Query("SELECT * FROM season_logs WHERE farmerId = :farmerId ORDER BY date DESC")
    fun getLogsByFarmerId(farmerId: Int): Flow<List<SeasonLogEntity>>

    @Query("SELECT * FROM season_logs WHERE id = :id")
    suspend fun getLogById(id: Int): SeasonLogEntity?

    @Query("DELETE FROM season_logs WHERE farmerId = :farmerId")
    suspend fun deleteLogsByFarmerId(farmerId: Int)

    @Query("DELETE FROM season_logs")
    suspend fun deleteAllLogs()
}
