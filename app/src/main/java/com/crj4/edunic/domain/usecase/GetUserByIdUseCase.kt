package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.model.User
import com.crj4.edunic.domain.repository.AuthRepository


class GetUserByIdUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: String): User? {
        return repository.getUserById(userId)
    }
}