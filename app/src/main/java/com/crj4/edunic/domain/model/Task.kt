package com.crj4.edunic.domain.model

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val teacherId: String = "",
    val groupId: String = "",
    val maxScore: Double = 0.0,
    val dueDate: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
