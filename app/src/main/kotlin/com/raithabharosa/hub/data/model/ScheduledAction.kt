package com.raithabharosa.hub.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_actions")
data class ScheduledAction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val notes: String?,
    val epochMillis: Long,
    val createdAt: Long = System.currentTimeMillis()
)
