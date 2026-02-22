package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.model.User
import com.crj4.edunic.domain.repository.AuthRepository

class UpdateUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return repository.updateUser(user)
    }
}

