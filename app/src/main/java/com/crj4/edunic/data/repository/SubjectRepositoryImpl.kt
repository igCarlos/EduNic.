package com.crj4.edunic.data.repository

import com.crj4.edunic.data.remote.FirestoreDataSource
import com.crj4.edunic.domain.model.Subject
import com.crj4.edunic.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow

class SubjectRepositoryImpl(
    private val firestoreDataSource: FirestoreDataSource
) : SubjectRepository {

    override suspend fun registerSubject(subject: Subject): Result<String> {
        return firestoreDataSource.saveSubject(subject)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return firestoreDataSource.getAllSubjects()
    }

    override suspend fun deleteSubject(id: String): Result<Unit> {
        return firestoreDataSource.deleteSubject(id)
    }

    override suspend fun updateSubject(subject: Subject): Result<Unit> {
        return firestoreDataSource.updateSubject(subject)
    }
}