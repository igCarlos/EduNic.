package com.crj4.edunic.data.remote


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): String? {
        return auth.currentUser?.uid
    }

    suspend fun logout() {
        auth.signOut()
    }

    suspend fun register(email: String, password: String): Result<String> {
        return try {
            // Crear usuario en FirebaseAuth
            val result = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .await()
            val uid = result.user?.uid ?: throw Exception("User creation failed")

            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
