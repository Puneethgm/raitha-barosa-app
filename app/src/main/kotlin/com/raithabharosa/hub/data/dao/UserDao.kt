package com.raithabharosa.hub.data.dao

import androidx.room.*
import com.raithabharosa.hub.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): User?

    @Query("SELECT count(*) FROM users")
    suspend fun count(): Int

    @Update
    suspend fun update(user: User): Int

    @Query("UPDATE users SET passwordHash = :passwordHash WHERE id = :id")
    suspend fun updatePassword(id: Int, passwordHash: String)

    @Query("UPDATE users SET username = :username WHERE id = :id")
    suspend fun updateUsername(id: Int, username: String)
}
