package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.repository.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logout()
}
