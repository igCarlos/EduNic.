package com.crj4.edunic.domain.usecase

import com.crj4.edunic.domain.repository.AuthRepository
import com.google.firebase.Timestamp

class RegisterCaseUse(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        name: String,
        lastname: String,
        dateOfBirth: Timestamp,
        image: String,
        email: String,
        password: String,
        role: String
    ) = repository.register(name, lastname,dateOfBirth,image,email, password, role)
}
