package com.crj4.edunic.domain.model

data class Subject(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val teacherId: String = "",
    val teacherName: String = "",
    val gradeId: String = "",
    val gradeName: String = "",
    val isActive: Boolean = true
)