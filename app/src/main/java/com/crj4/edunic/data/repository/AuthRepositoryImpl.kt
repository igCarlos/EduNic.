package com.crj4.edunic.data.repository

import com.crj4.edunic.data.remote.FirebaseAuthDataSource
import com.crj4.edunic.data.remote.FirestoreDataSource
import com.crj4.edunic.domain.model.User
import com.crj4.edunic.domain.repository.AuthRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val authDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        return authDataSource.login(email, password)
    }

    override suspend fun getUserRole(uid: String): Result<String> {
        return firestoreDataSource.getUserRole(uid)
    }

    override fun getCurrentUser(): String? {
        return authDataSource.getCurrentUser()
    }

    override suspend fun logout() {
        authDataSource.logout()
    }

    override suspend fun register(
        name: String,
        lastname: String,
        dateOfBirth: Timestamp,
        image: String,
        email: String,
        password: String,
        role: String
    ): Result<String> {
        return try {
            val uidResult = authDataSource.register(email, password)
            if (uidResult.isFailure) return uidResult
            val uid = uidResult.getOrThrow()

            val saveResult = firestoreDataSource.saveUser(
                uid = uid,
                name = name,
                lastname = lastname,
                dateOfBirth = dateOfBirth,
                image = image,
                email = email,
                role = role
            )

            if (saveResult.isFailure) {
                auth.currentUser?.delete()?.await()
                return Result.failure(saveResult.exceptionOrNull()!!)
            }

            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = suspendCancellableCoroutine { cont ->
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(Result.success(Unit)) {}
                } else {
                    cont.resume(Result.failure(task.exception ?: Exception("Error al enviar correo"))) {}
                }
            }
    }

    override suspend fun getUserData(uid: String): Result<Map<String, Any>> {
        return firestoreDataSource.getUser(uid)
    }

    override fun getAllUsers(): Flow<List<User>> {
        return firestoreDataSource.getAllUsers()
    }

    override suspend fun deleteUser(id: String): Result<Unit> {
        return firestoreDataSource.deleteUser(id)
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return firestoreDataSource.updateUser(user)
    }

    override suspend fun getUserById(userId: String): User? {
        return firestoreDataSource.getUserById(userId)
    }

}
