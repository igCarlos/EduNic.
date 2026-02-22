package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.repository.AuthRepository

class DeleteUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(uid: String): Result<Unit> {
        return repository.deleteUser(uid)
    }
}

