package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.repository.AuthRepository

class SendPasswordResetUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.sendPasswordReset(email)
    }
}