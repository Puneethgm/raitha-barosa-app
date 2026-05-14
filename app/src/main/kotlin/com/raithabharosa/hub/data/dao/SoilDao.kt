package com.raithabharosa.hub.data.dao
import androidx.room.*
import com.raithabharosa.hub.data.model.SoilRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface SoilDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoilRecord(soil: SoilRecord): Long
    @Query("SELECT * FROM soil_records WHERE seasonId = :seasonId ORDER BY recordedAt DESC")
    fun getSoilRecords(seasonId: Int): Flow<List<SoilRecord>>
    @Query("SELECT * FROM soil_records WHERE seasonId = :seasonId ORDER BY recordedAt DESC LIMIT 1")
    fun getLatestSoilRecord(seasonId: Int): Flow<SoilRecord?>
    @Update
    suspend fun updateSoilRecord(soil: SoilRecord)
    @Delete
    suspend fun deleteSoilRecord(soil: SoilRecord)
}
