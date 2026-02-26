package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.model.Subject
import com.crj4.edunic.domain.repository.SubjectRepository


class RegisterSubjectUseCase(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(subject: Subject): Result<String> {
        return repository.registerSubject(subject)
    }
}