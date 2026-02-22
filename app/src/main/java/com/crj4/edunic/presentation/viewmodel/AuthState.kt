package com.crj4.edunic.presentation.viewmodel

import com.crj4.edunic.domain.model.Role

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val role: Role) : AuthState()
    data class Error(val message: String) : AuthState()

    data class ResetEmailSent(val message: String) : AuthState() // ← NUEVO
}

