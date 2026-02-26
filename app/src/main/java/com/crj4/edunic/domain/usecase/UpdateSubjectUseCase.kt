package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.model.Subject
import com.crj4.edunic.domain.repository.SubjectRepository

class UpdateSubjectUseCase(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(subject: Subject): Result<Unit> {
        return repository.updateSubject(subject)
    }
}