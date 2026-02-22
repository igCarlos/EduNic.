package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.repository.AuthRepository

class GetUserDataUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(uid: String): Result<Map<String, Any>> {
        return repository.getUserData(uid)
    }
}
