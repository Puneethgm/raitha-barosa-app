package com.raithabharosa.hub.data.repository

import com.raithabharosa.hub.data.dao.ScheduledActionDao
import com.raithabharosa.hub.data.model.ScheduledAction

class ScheduledActionRepository(private val dao: ScheduledActionDao) {
    suspend fun add(action: ScheduledAction): Result<ScheduledAction> {
        return try {
            val id = dao.insert(action)
            Result.success(action.copy(id = id.toInt()))
        } catch (t: Throwable) { Result.failure(t) }
    }

    suspend fun listForUser(userId: Int): Result<List<ScheduledAction>> {
        return try { Result.success(dao.findForUser(userId)) } catch (t: Throwable) { Result.failure(t) }
    }

    suspend fun delete(action: ScheduledAction) {
        dao.delete(action)
    }
}
