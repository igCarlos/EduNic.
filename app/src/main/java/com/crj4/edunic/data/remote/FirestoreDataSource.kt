package com.crj4.edunic.data.remote

import com.crj4.edunic.domain.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = FirebaseFirestore
        .getInstance()
        .collection("users")

    suspend fun getUserRole(uid: String): Result<String> {
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val role = snapshot.getString("role") ?: "user"
            Result.success(role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUser(
        uid: String,
        name: String,
        lastname: String,
        dateOfBirth: Timestamp,
        image: String,
        email: String,
        role: String
    ): Result<Unit> {

        return try {

            val userMap = mapOf(
                "uid" to uid,
                "name" to name,
                "lastname" to lastname,
                "dateOfBirth" to dateOfBirth,
                "image" to image,
                "email" to email,
                "role" to role,
                "createdAt" to Timestamp.now()
            )

            firestore.collection("users")
                .document(uid)
                .set(userMap)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(uid: String): Result<Map<String, Any>> {
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val data = snapshot.data
            if (data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    suspend fun getAllUsersT(): Result<List<User>> {
//        return try {
//            val snapshot = firestore.collection("users").get().await()
//            val usersList = snapshot.toObjects(User::class.java)
//            Result.success(usersList)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    fun getAllUsers(): Flow<List<User>> = callbackFlow {

        val listener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val usersList = snapshot.documents.mapNotNull { document ->
                        document.toObject(User::class.java)?.copy(
                            uid = document.id
                        )
                    }

                    trySend(usersList).isSuccess
                }
            }

        awaitClose {
            listener.remove()
        }
    }



    suspend fun deleteUser(uid: String): Result<Unit> {
        return try {
            usersCollection.document(uid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener usuario por ID
    suspend fun getUserById(userId: String): User {
        val doc = usersCollection.document(userId).get().await()
        return doc.toObject<User>() ?: throw Exception("Usuario no encontrado")
    }







}
