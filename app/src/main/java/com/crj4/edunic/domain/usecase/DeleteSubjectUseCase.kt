package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.repository.SubjectRepository

class DeleteSubjectUseCase(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteSubject(id)
    }
}