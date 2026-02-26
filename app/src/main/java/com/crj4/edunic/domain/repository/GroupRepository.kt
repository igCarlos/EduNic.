package com.crj4.edunic.domain.repository

import com.crj4.edunic.domain.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun createGroup(group: Group) : Result<String>

    fun getAllGroups(): Flow<List<Group>>

    suspend fun deleteGroup(id: String): Result<Unit>

    suspend fun updateGroup(group: Group): Result<Unit>
}