package com.raithabharosa.hub.data.dao

import androidx.room.*
import com.raithabharosa.hub.data.model.ScheduledAction

@Dao
interface ScheduledActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(action: ScheduledAction): Long

    @Query("SELECT * FROM scheduled_actions WHERE userId = :userId ORDER BY epochMillis ASC")
    suspend fun findForUser(userId: Int): List<ScheduledAction>

    @Delete
    suspend fun delete(action: ScheduledAction)
}
