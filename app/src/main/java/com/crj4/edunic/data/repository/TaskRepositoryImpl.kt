package com.crj4.edunic.data.repository

import com.crj4.edunic.data.remote.FirestoreDataSource
import com.crj4.edunic.domain.model.Task
import com.crj4.edunic.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private  val firestoreDataSource: FirestoreDataSource
) : TaskRepository {
    override suspend fun createTask(task: Task): Result<String> {
        return firestoreDataSource.createTask(task)
    }

    override fun getAllTask(): Flow<List<Task>> {
        return firestoreDataSource.getAllTasks()
    }

    override suspend fun deleteTask(id: String): Result<Unit> {
        return firestoreDataSource.deleteTask(id)
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return firestoreDataSource.updateTask(task)
    }

}