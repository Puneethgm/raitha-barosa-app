package com.example.raitha_bharosa.data.local.dao

import androidx.room.*
import com.example.raitha_bharosa.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getUserByPhoneNumber(phoneNumber: String): UserEntity?

    @Query("SELECT * FROM users WHERE name = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isVerified = :isVerified WHERE phoneNumber = :phoneNumber")
    suspend fun updateVerificationStatus(phoneNumber: String, isVerified: Boolean)

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber OR name = :username LIMIT 1")
    suspend fun getUserByPhoneOrUsername(phoneNumber: String, username: String): UserEntity?
}
