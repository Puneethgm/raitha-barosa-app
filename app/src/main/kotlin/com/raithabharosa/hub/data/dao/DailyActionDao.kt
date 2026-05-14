package com.raithabharosa.hub.data.dao
import androidx.room.*
import com.raithabharosa.hub.data.model.DailyAction
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: DailyAction): Long
    @Query("SELECT * FROM daily_actions WHERE seasonId = :seasonId ORDER BY dayNumber ASC")
    fun getActionsForSeason(seasonId: Int): Flow<List<DailyAction>>
    @Query("SELECT * FROM daily_actions WHERE seasonId = :seasonId AND dayNumber = :dayNumber")
    suspend fun getActionForDay(seasonId: Int, dayNumber: Int): DailyAction?
    @Update
    suspend fun updateAction(action: DailyAction)
    @Delete
    suspend fun deleteAction(action: DailyAction)
}
