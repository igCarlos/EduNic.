package com.crj4.edunic.domain.model

data class Group(
    val id: String = "",
    val name: String = "",
    val gradeId: String = "",
    val gradeName: String = "",
    val tutorId: String = "",
    val tutorName: String = "",
    val isActive: Boolean = true
)