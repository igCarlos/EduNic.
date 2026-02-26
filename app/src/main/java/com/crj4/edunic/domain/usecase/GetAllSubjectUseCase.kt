package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.model.Subject
import com.crj4.edunic.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow

class GetAllSubjectsUseCase(
    private val repository: SubjectRepository
) {
    operator fun invoke(): Flow<List<Subject>> {
        return repository.getAllSubjects()
    }
}