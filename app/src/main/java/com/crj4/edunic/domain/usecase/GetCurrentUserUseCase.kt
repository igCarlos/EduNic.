package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.getCurrentUser()
}
