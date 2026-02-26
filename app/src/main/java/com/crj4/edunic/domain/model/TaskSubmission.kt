package com.crj4.edunic.domain.model

data class TaskSubmission(
    val id: String = "",
    val taskId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val score: Double = 0.0,
    val submittedAt: Long = 0L,
    val isGraded: Boolean = false
)
