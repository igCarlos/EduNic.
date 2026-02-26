package com.crj4.edunic.domain.repository

import com.crj4.edunic.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    suspend fun registerSubject(subject: Subject): Result<String>

    fun getAllSubjects(): Flow<List<Subject>>

    suspend fun deleteSubject(id: String): Result<Unit>

    suspend fun updateSubject(subject: Subject): Result<Unit>
}