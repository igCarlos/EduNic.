package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.repository.AuthRepository

class GetUserRoleUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(uid: String) =
        repository.getUserRole(uid)
}
