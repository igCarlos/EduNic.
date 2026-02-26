package com.crj4.edunic.data.repository

import com.crj4.edunic.data.remote.FirestoreDataSource
import com.crj4.edunic.domain.model.Group
import com.crj4.edunic.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow

class GroupRepositoryImpl(
    private val firestoreDataSource: FirestoreDataSource
) : GroupRepository {
    override suspend fun createGroup(group: Group): Result<String> {
        return firestoreDataSource.createGroup(group)
    }

    override fun getAllGroups(): Flow<List<Group>> {
        return firestoreDataSource.getAllGroups()
    }

    override suspend fun deleteGroup(id: String): Result<Unit> {
        return firestoreDataSource.deleteGroup(id)
    }

    override suspend fun updateGroup(group: Group): Result<Unit> {
        return firestoreDataSource.updateGroup(group)
    }
}