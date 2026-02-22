package com.crj4.edunic.domain.manager

import com.crj4.edunic.domain.model.Permission
import com.crj4.edunic.domain.model.Role

object RoleManager {

    private val rolePermissions = mapOf(
        Role.ADMIN to setOf(
            Permission.CREATE_USER,
            Permission.UPDATE_USER, // 🔹 ADMIN puede actualizar usuarios
            Permission.DELETE_USER,
            Permission.VIEW_GRADES,
            Permission.EDIT_GRADES,
            Permission.VIEW_DASHBOARD
        ),

        Role.TUTOR to setOf(
            Permission.UPDATE_USER, // 🔹 si quieres que los tutores también puedan editar usuarios
            Permission.VIEW_GRADES,
            Permission.EDIT_GRADES,
            Permission.VIEW_DASHBOARD
        ),

        Role.STUDENT to setOf(
            Permission.VIEW_GRADES
        )
    )

    fun hasPermission(role: Role, permission: Permission): Boolean {
        return rolePermissions[role]?.contains(permission) ?: false
    }

    // Devuelve todos los permisos de un rol
    fun hasPermissionSet(role: Role): Set<Permission> {
        return rolePermissions[role] ?: emptySet()
    }
}
