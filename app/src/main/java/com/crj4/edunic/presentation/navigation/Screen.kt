package com.crj4.edunic.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object AdminHome : Screen("admin_home")
    object TutorHome : Screen("tutor_home")
    object StudentHome : Screen("student_home")
    object UserHome : Screen("user_home")
    object Register : Screen("register")

    object RegisterUser : Screen("register_user")
    object EditarUser : Screen("register_user/{userId}")
    object ResetPassword : Screen("forgot_password")
    object UserProfile : Screen("rofile")
    object Materia : Screen("subject")
}