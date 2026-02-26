package com.crj4.edunic.di

import com.crj4.edunic.data.remote.FirebaseAuthDataSource
import com.crj4.edunic.data.remote.FirestoreDataSource
import com.crj4.edunic.data.repository.AuthRepositoryImpl
import com.crj4.edunic.data.repository.SubjectRepositoryImpl
import com.crj4.edunic.domain.usecase.DeleteSubjectUseCase
import com.crj4.edunic.domain.usecase.DeleteUserUseCase
import com.crj4.edunic.domain.usecase.GetAllSubjectsUseCase
import com.crj4.edunic.domain.usecase.GetAllUsersUseCase
import com.crj4.edunic.domain.usecase.GetCurrentUserUseCase
import com.crj4.edunic.domain.usecase.GetUserByIdUseCase
import com.crj4.edunic.domain.usecase.GetUserDataUseCase
import com.crj4.edunic.domain.usecase.GetUserRoleUseCase
import com.crj4.edunic.domain.usecase.LoginUseCase
import com.crj4.edunic.domain.usecase.LogoutUseCase
import com.crj4.edunic.domain.usecase.RegisterCaseUse
import com.crj4.edunic.domain.usecase.RegisterSubjectUseCase
import com.crj4.edunic.domain.usecase.SendPasswordResetUseCase
import com.crj4.edunic.domain.usecase.UpdateSubjectUseCase
import com.crj4.edunic.domain.usecase.UpdateUserUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlin.getValue


object AppModule {

    // DataSources
    private val auth by lazy {FirebaseAuth.getInstance()}
    private val authDataSource by lazy { FirebaseAuthDataSource() }
    private val firestoreDataSource by lazy { FirestoreDataSource() }

    // Repository
    private val authRepository by lazy {
        AuthRepositoryImpl(auth,authDataSource, firestoreDataSource)
    }

    private val registersubjectRepository by lazy {
        SubjectRepositoryImpl(firestoreDataSource)
    }

    // UseCases
    val loginUseCase by lazy { LoginUseCase(authRepository) }
    val getUserRoleUseCase by lazy { GetUserRoleUseCase(authRepository) }
    val getCurrentUserUseCase by lazy { GetCurrentUserUseCase(authRepository) }
    val logoutUseCase by lazy { LogoutUseCase(authRepository) }

    val registerCaseUse by lazy { RegisterCaseUse(authRepository) }

    val sendPasswordResetUseCase by lazy {
        SendPasswordResetUseCase(authRepository)
    }

    val getUserDataUseCase = GetUserDataUseCase(authRepository) // 🔹 nuevo caso de uso

    // -------------------------
    // USER USE CASE
    // -------------------------
    val getAllUsersUseCase by lazy {
        GetAllUsersUseCase(authRepository)
    }

    val deleteUserUseCase by lazy {
        DeleteUserUseCase(authRepository)
    }

    val updateUserUseCase by lazy {
        UpdateUserUseCase(authRepository)
    }

    val GetUserByIdUseCase by lazy {
        GetUserByIdUseCase(authRepository)
    }

    // -------------------------
    // SUBJECT USE CASE
    // -------------------------
    val registerSubjectUseCase by lazy {
        RegisterSubjectUseCase(registersubjectRepository)
    }

    val getAllSubjectsUseCase by lazy {
        GetAllSubjectsUseCase(registersubjectRepository)
    }

    val deleteSubjectUseCase by lazy {
        DeleteSubjectUseCase(registersubjectRepository)
    }

    val updateSubjectUseCase by lazy {
        UpdateSubjectUseCase(registersubjectRepository)
    }
}
