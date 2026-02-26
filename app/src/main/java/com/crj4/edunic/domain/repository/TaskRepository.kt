package com.crj4.edunic.domain.repository

import com.crj4.edunic.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun createTask(task: Task) : Result<String>

    fun getAllTask(): Flow<List<Task>>

    suspend fun deleteTask(id: String): Result<Unit>

    suspend fun updateTask(task: Task): Result<Unit>
}