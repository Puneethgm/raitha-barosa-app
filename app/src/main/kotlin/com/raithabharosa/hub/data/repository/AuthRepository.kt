package com.raithabharosa.hub.data.repository

import com.raithabharosa.hub.data.dao.UserDao
import com.raithabharosa.hub.data.model.User
import java.security.MessageDigest

class AuthRepository(private val userDao: UserDao) {
    suspend fun register(username: String, phone: String, password: String): Result<User> {
        val existing = userDao.findByUsername(username)
        if (existing != null) return Result.failure(Exception("username_taken"))
        val hash = sha256(password)
        val id = userDao.insert(User(username = username, phone = phone, passwordHash = hash))
        val user = userDao.findById(id.toInt())!!
        return Result.success(user)
    }

    suspend fun authenticate(username: String, password: String): Result<User> {
        val user = userDao.findByUsername(username) ?: return Result.failure(Exception("not_found"))
        val hash = sha256(password)
        return if (user.passwordHash == hash) Result.success(user) else Result.failure(Exception("invalid_credentials"))
    }

    suspend fun changePassword(userId: Int, oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            val user = userDao.findById(userId) ?: return Result.failure(Exception("not_found"))
            if (user.passwordHash != sha256(oldPassword)) return Result.failure(Exception("invalid_credentials"))
            val newHash = sha256(newPassword)
            val updatedUser = user.copy(passwordHash = newHash)
            userDao.update(updatedUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changeUsername(userId: Int, newUsername: String): Result<Unit> {
        return try {
            val existing = userDao.findByUsername(newUsername)
            if (existing != null && existing.id != userId) return Result.failure(Exception("username_taken"))
            val user = userDao.findById(userId) ?: return Result.failure(Exception("not_found"))
            val updatedUser = user.copy(username = newUsername)
            userDao.update(updatedUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
