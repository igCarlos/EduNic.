package com.crj4.edunic.domain.model

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val name: String = "",
    val lastname: String = "",
    val dateOfBirth: Timestamp? = null,
    val image: String = "",
    val email: String = "",
    val role: String = ""
)
