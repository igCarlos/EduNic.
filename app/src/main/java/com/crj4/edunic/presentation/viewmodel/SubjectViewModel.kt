package com.crj4.edunic.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crj4.edunic.di.AppModule
import com.crj4.edunic.domain.manager.RoleManager
import com.crj4.edunic.domain.model.Permission
import com.crj4.edunic.domain.model.Role
import com.crj4.edunic.domain.model.Subject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SubjectViewModel : ViewModel() {

    private val registerSubject = AppModule.registerSubjectUseCase
    private val getAllSubjectsUseCase = AppModule.getAllSubjectsUseCase
    private val deleteSubjectUseCase = AppModule.deleteSubjectUseCase
    private val updateSubjectUseCase = AppModule.updateSubjectUseCase
    private val getUserRoleUseCase = AppModule.getUserRoleUseCase

    // Current user role
    private val _currentRole = MutableStateFlow<Role?>(null)
    val currentRole: StateFlow<Role?> get() = _currentRole

    // Subjects
    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> get() = _subjects

    // Loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    // Error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    init {
        viewModelScope.launch {
            loadUserRole()
            loadSubjects()
        }
    }

    private suspend fun loadUserRole() {
        val uid = AppModule.getCurrentUserUseCase() ?: run {
            _errorMessage.value = "Usuario no autenticado"
            return
        }

        val result = getUserRoleUseCase(uid)
        result.onSuccess { roleString ->
            _currentRole.value = Role.valueOf(roleString)
        }
        result.onFailure {
            _errorMessage.value = it.message
        }
    }

    private suspend fun loadSubjects() {
        // Solo carga inicial
        _subjects.value = getAllSubjectsUseCase().first()

        // Si quieres updates en tiempo real, puedes usar:
        getAllSubjectsUseCase().collect { _subjects.value = it }
    }



    // Función para verificar permisos fácilmente desde la UI
    fun can(permission: Permission): Boolean {
        val role = _currentRole.value ?: return false
        return RoleManager.hasPermission(role, permission)
    }

    fun saveSubject(subject: Subject) {
        val role = _currentRole.value ?: return
        if (!RoleManager.hasPermission(role, Permission.CREATE_SUBJECT)) {
            _errorMessage.value = "No tienes permiso para crear materias"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = registerSubject(subject)
                result.onFailure { _errorMessage.value = it.message }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSubject(id: String) {
        val role = _currentRole.value ?: return
        if (!RoleManager.hasPermission(role, Permission.DELETE_SUBJECT)) {
            _errorMessage.value = "No tienes permiso para eliminar materias"
            return
        }

        viewModelScope.launch {
            try {
                deleteSubjectUseCase(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun updateSubject(subject: Subject) {
        val role = _currentRole.value ?: return
        if (!RoleManager.hasPermission(role, Permission.UPDATE_SUBJECT)) {
            _errorMessage.value = "No tienes permiso para actualizar materias"
            return
        }

        viewModelScope.launch {
            try {
                updateSubjectUseCase(subject)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}