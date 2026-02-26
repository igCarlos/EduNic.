package com.crj4.edunic.data.remote

import com.crj4.edunic.domain.model.Group
import com.crj4.edunic.domain.model.Subject
import com.crj4.edunic.domain.model.Task
import com.crj4.edunic.domain.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = FirebaseFirestore
        .getInstance()
        .collection("users")

    /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////

    // Obtener Los Roles
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

    /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////

    // Guardar Usuario
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

    // Obtener Usuario Mediante El Id
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


    // Mostrar Todos Los Usuarios
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


    // Eliminar Usuario
    suspend fun deleteUser(uid: String): Result<Unit> {
        return try {
            usersCollection.document(uid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar Usuario
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener usuario por ID
    suspend fun getUserById(userId: String): User? {
        val doc = usersCollection.document(userId).get().await()
        return doc.toObject(User::class.java)
    }

    ///////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////
    val subjectName = "Subject"

    // Guardar Materias
    suspend fun saveSubject(subject: Subject): Result<String> {
        return try {

            val docRef = firestore.collection(subjectName).document()

            val subjectWithId = subject.copy(
                id = docRef.id
            )

            docRef.set(subjectWithId).await()

            Result.success(docRef.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todas las materias
    fun getAllSubjects(): Flow<List<Subject>> = callbackFlow {

        val listener = firestore.collection(subjectName)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val subjectsList = snapshot.documents.mapNotNull { document ->
                        document.toObject(Subject::class.java)
                    }

                    trySend(subjectsList).isSuccess
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    // Eliminar Materia
    suspend fun deleteSubject(id: String): Result<Unit> {
        return try {
            firestore.collection(subjectName)
                .document(id)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar Materia
    suspend fun updateSubject(subject: Subject): Result<Unit> {
        return try {
            firestore.collection(subjectName)
                .document(subject.id)
                .set(subject)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    ///////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////
    val groupName = "Group"

    // Guardar Grupo
    suspend fun createGroup(group: Group): Result<String> {
        return try {

            val docRef = firestore.collection(groupName).document()

            val subjectWithId = group.copy(
                id = docRef.id
            )

            docRef.set(subjectWithId).await()

            Result.success(docRef.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todas las Grupo
    fun getAllGroups(): Flow<List<Group>> = callbackFlow {

        val listener = firestore.collection(groupName)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val groupsList: List<Group> = snapshot.documents.mapNotNull { document ->
                        document.toObject(Subject::class.java) as Group?
                    }

                    trySend(groupsList).isSuccess
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    // Eliminar Grupo
    suspend fun deleteGroup(id: String): Result<Unit> {
        return try {
            firestore.collection(groupName)
                .document(id)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar Grupo
    suspend fun updateGroup(group: Group): Result<Unit> {
        return try {
            firestore.collection(groupName)
                .document(group.id)
                .set(group)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    ///////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////
    val taskName = "Task"

    // Guardar Task
    suspend fun createTask(task: Task): Result<String> {
        return try {

            val docRef = firestore.collection(taskName).document()

            val subjectWithId = task.copy(
                id = docRef.id
            )

            docRef.set(subjectWithId).await()

            Result.success(docRef.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todas las Task
    fun getAllTasks(): Flow<List<Task>> = callbackFlow {

        val listener = firestore.collection(taskName)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val tasksList: List<Task> = snapshot.documents.mapNotNull { document ->
                        document.toObject(Subject::class.java) as Task?
                    }

                    trySend(tasksList).isSuccess
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    // Eliminar Task
    suspend fun deleteTask(id: String): Result<Unit> {
        return try {
            firestore.collection(taskName)
                .document(id)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar Grupo
    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            firestore.collection(taskName)
                .document(task.id)
                .set(task)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }






}
