package com.crj4.edunic.presentation.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crj4.edunic.di.AppModule
import com.crj4.edunic.domain.model.Role
import com.crj4.edunic.domain.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.crj4.edunic.domain.manager.RoleManager
import com.crj4.edunic.domain.model.Permission
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AuthViewModel : ViewModel() {

    private val loginUseCase = AppModule.loginUseCase
    private val getUserRoleUseCase = AppModule.getUserRoleUseCase
    private val getCurrentUserUseCase = AppModule.getCurrentUserUseCase
    private val logoutUseCase = AppModule.logoutUseCase
    private val sendPasswordResetUseCase = AppModule.sendPasswordResetUseCase
    private val registerCaseUse = AppModule.registerCaseUse
    private val getUserDataUseCase = AppModule.getUserDataUseCase
    private val getAllUsersUseCase = AppModule.getAllUsersUseCase  // 👈 agregar esto

    private val getUserByIdUseCase = AppModule.GetUserByIdUseCase

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    var currentRole by mutableStateOf<Role?>(null)
        private set

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var usersLoading by mutableStateOf(false)
        private set

    var usersError by mutableStateOf<String?>(null)
        private set

    private val deleteUserUseCase = AppModule.deleteUserUseCase
    private val updateUserUseCase = AppModule.updateUserUseCase
    val searchQuery = MutableStateFlow("")
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val usersRealTime: StateFlow<List<User>> = _users

    val filteredUsers: StateFlow<List<User>> =
        combine(_users, searchQuery) { usersList, query ->

            if (query.isBlank()) {
                usersList
            } else {
                usersList.filter { user ->
                    user.name.contains(query, ignoreCase = true) ||
                            user.lastname.contains(query, ignoreCase = true) ||
                            user.email.contains(query, ignoreCase = true)
                }
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser


    init {
        checkSession()

        viewModelScope.launch {
            getAllUsersUseCase().collect { userList ->
                _users.value = userList
            }
        }
    }

    private fun checkSession() {
        val uid = getCurrentUserUseCase()

        if (uid == null) {
            authState = AuthState.Unauthenticated
            return
        }

        viewModelScope.launch {
            loadUserRole(uid)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authState = AuthState.Loading

            loginUseCase(email, password)
                .onSuccess { uid ->
                    loadUserRole(uid)
                }
                .onFailure {
                    authState = AuthState.Error(it.message ?: "Login error")
                }
        }
    }

    private suspend fun loadUserRole(uid: String) {
        getUserRoleUseCase(uid)
            .onSuccess { roleString ->
                val role = Role.entries
                    .find { it.name == roleString.uppercase() }

                if (role != null) {
                    currentRole = role
                    authState = AuthState.Authenticated(role)
                } else {
                    authState = AuthState.Error("Invalid role")
                }
            }
            .onFailure {
                authState = AuthState.Error("Failed to get role")
            }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            currentRole = null
            authState = AuthState.Unauthenticated
        }
    }


    fun register(
        name: String,
        lastname: String,
        dateOfBirth: Timestamp,
        image: String,
        email: String,
        password: String,
        role: Role,
        onComplete: (Result<String>) -> Unit  // <- callback opcional
    ) {
        viewModelScope.launch {
            authState = AuthState.Loading

            registerCaseUse(name, lastname,dateOfBirth,image,email, password, role.name)

                .onSuccess { uid ->
                    currentRole = role
                    authState = AuthState.Authenticated(role)
                    // 🔹 No cambiamos currentRole ni authState
                    onComplete(Result.success(uid))
                }
                .onFailure {
                    authState = AuthState.Error(it.message ?: "Register error")
                }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            authState = AuthState.Loading
            FirebaseAuth.getInstance()
            val result = sendPasswordResetUseCase(email)
            authState = result.fold(
                onSuccess = { AuthState.ResetEmailSent("Correo enviado correctamente") },
                onFailure = { AuthState.Error(it.message ?: "Error al enviar correo") }
            )
        }
    }

    // 🔹 Usar casos de uso en lugar de repositorios directos
    suspend fun getUserData(uid: String): Result<Map<String, Any>> {
        return getUserDataUseCase(uid) // ahora retorna Result<Map<String, Any>>
    }

    fun getCurrentUser(): String? {
        return getCurrentUserUseCase()
    }

    fun getCurrentPermissions(): Set<Permission> {
        val role = currentRole ?: return emptySet()
        return RoleManager.hasPermissionSet(role)
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {

            val role = currentRole
            if (role == null || !RoleManager.hasPermission(role, Permission.DELETE_USER)) {
                usersError = "No tienes permiso para eliminar usuarios"
                return@launch
            }

            usersLoading = true
            usersError = null

            deleteUserUseCase(uid)
                .onSuccess {
                    // Filtrar la lista para eliminar el usuario borrado
                    _users.value = _users.value.filter { it.uid != uid }
                }
                .onFailure {
                    usersError = it.message
                }

            usersLoading = false
        }
    }


    // ----------------------------
    // Cargar usuario por ID
    // ----------------------------
    fun loadUserById(userId: String) {
        viewModelScope.launch {
            try {
                val user = getUserByIdUseCase(userId)
                _selectedUser.value = user
            } catch (e: Exception) {
                // Manejo de error ético y profesional
                usersError = "No se pudo cargar el usuario: ${e.message}"
            }
        }
    }

    // ----------------------------
    // Actualizar usuario
    // ----------------------------
    fun updateUser(user: User) {
        viewModelScope.launch {
            val role = currentRole
            val currentUid = getCurrentUser() // tu función que obtiene UID logueado

            val isAdmin = role != null &&
                    RoleManager.hasPermission(role, Permission.UPDATE_USER) // 🔹 Usar UPDATE_USER
            val isSelf = currentUid == user.uid

            if (!isAdmin && !isSelf) {
                usersError = "No tienes permiso para editar este usuario"
                return@launch
            }

            usersLoading = true
            usersError = null

            updateUserUseCase(user)
                .onSuccess {
                    // Actualizar la lista en tiempo real
                    _users.value = _users.value.map { if (it.uid == user.uid) user else it }
                    usersError = null
                }
                .onFailure {
                    usersError = it.message
                }

            usersLoading = false
        }
    }

    fun onSearchChange(query: String) {
        searchQuery.value = query
    }



}
