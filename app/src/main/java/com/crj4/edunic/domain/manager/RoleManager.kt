package com.crj4.edunic.domain.manager

import com.crj4.edunic.domain.model.Permission
import com.crj4.edunic.domain.model.Role

object RoleManager {

    private val rolePermissions = mapOf(
        Role.ADMIN to setOf(
            Permission.VIEW_USER,
            Permission.CREATE_USER,
            Permission.UPDATE_USER,
            Permission.DELETE_USER,
            Permission.VIEW_SUBJECT,
            Permission.CREATE_SUBJECT,
            Permission.UPDATE_SUBJECT,
            Permission.DELETE_SUBJECT,
            Permission.VIEW_DASHBOARD
        ),

        Role.TUTOR to setOf(
            Permission.UPDATE_SUBJECT,
            Permission.VIEW_DASHBOARD
        ),

        Role.STUDENT to setOf(
            Permission.VIEW_SUBJECT
        )
    )

    fun hasPermission(role: Role, permission: Permission): Boolean =
        rolePermissions[role]?.contains(permission) == true

    // Devuelve todos los permisos de un rol
    fun hasPermissionSet(role: Role): Set<Permission> {
        return rolePermissions[role] ?: emptySet()
    }
}
