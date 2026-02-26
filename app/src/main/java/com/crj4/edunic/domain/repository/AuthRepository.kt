package com.crj4.edunic.domain.repository

import com.crj4.edunic.domain.model.User
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<String>

    suspend fun getUserRole(uid: String): Result<String>

    fun getCurrentUser(): String?   // <- NUEVO

    suspend fun logout()

    suspend fun register(
        name: String,
        lastname: String,
        dateOfBirth: Timestamp,
        image: String,
        email: String,
        password: String,
        role: String
    ): Result<String>


//    suspend fun sendPasswordReset(email: String): Result<Unit>

    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun getUserData(uid: String): Result<Map<String, Any>>

//    suspend fun getAllUsersT(): Result<List<User>>

    fun getAllUsers(): Flow<List<User>>
    suspend fun deleteUser(uid: String): Result<Unit>

    suspend fun updateUser(user: User): Result<Unit>

    suspend fun getUserById(userId: String): User?


}

