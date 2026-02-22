package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.model.User
import com.crj4.edunic.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow


class GetAllUsersUseCase(
    private val repository: AuthRepository
) {

//    suspend operator fun invoke(): Result<List<User>> {
//        return repository.getAllUsers()
//    }

    operator fun invoke(): Flow<List<User>> {
        return repository.getAllUsers()
    }



}
