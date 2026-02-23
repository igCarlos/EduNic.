package com.crj4.edunic.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.crj4.edunic.presentation.screen.login.LoginScreen
import com.crj4.edunic.presentation.screen.splash.SplashScreen
import com.crj4.edunic.presentation.screen.home.AdminHome
import com.crj4.edunic.presentation.screen.home.TutorHome
import com.crj4.edunic.presentation.screen.home.StudentHome
import com.crj4.edunic.presentation.screen.home.UserHome
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import com.crj4.edunic.domain.model.Role
import com.crj4.edunic.presentation.components.BottomNavItem
import com.crj4.edunic.presentation.components.MainScaffold
import com.crj4.edunic.presentation.screen.register.RegisterScreen
import com.crj4.edunic.presentation.viewmodel.AuthState
import com.crj4.edunic.presentation.screen.profile.UserProfileScreen
import com.crj4.edunic.presentation.screen.resetpassword.ForgotPasswordScreen

@Composable
fun NavGraph(navController: NavHostController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState
    var navigated by remember { mutableStateOf(false) }

    // Rutas que deben tener MainScaffold
    val scaffoldRoutes = listOf(
        Screen.AdminHome.route,
        Screen.TutorHome.route,
        Screen.StudentHome.route,
        Screen.UserHome.route,
        Screen.UserProfile.route,
        Screen.RegisterUser.route
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // SplashScreen
        composable(Screen.Splash.route) {
            LaunchedEffect(authState) {
                if (!navigated && authState !is AuthState.Loading) {
                    when (authState) {
                        is AuthState.Authenticated -> {
                            val destination = when (authState.role) {
                                Role.ADMIN -> Screen.AdminHome.route
                                Role.TUTOR -> Screen.TutorHome.route
                                Role.STUDENT -> Screen.StudentHome.route
//                                Role.USER -> Screen.UserHome.route
                            }
                            navController.navigate(destination) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                        is AuthState.Unauthenticated -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                        else -> {}
                    }
                    navigated = true
                }
            }

            SplashScreen()
        }

        // Pantallas que necesitan MainScaffold
        scaffoldRoutes.forEach { route ->
            composable(route) {
                MainScaffold(
                    navController = navController,
                    authViewModel = authViewModel,
                    currentRoute = route,
                    bottomNavItems = listOf(
                        BottomNavItem(Screen.AdminHome.route, Icons.Default.Home, "Inicio"),
                        BottomNavItem(Screen.UserProfile.route, Icons.Default.Person, "Perfil"),
//                        BottomNavItem("settings", Icons.Default.Settings, "Ajustes")

                    )
                ) {
                    when (route) {
                        Screen.AdminHome.route -> AdminHome(authViewModel, navController)
                        Screen.TutorHome.route -> TutorHome(authViewModel, navController)
                        Screen.StudentHome.route -> StudentHome(authViewModel, navController)
                        Screen.UserHome.route -> UserHome(authViewModel, navController)
                        Screen.UserProfile.route -> UserProfileScreen(authViewModel, navController)
                        Screen.RegisterUser.route -> RegisterScreen(null,authViewModel) { roleRoute ->
                            navController.navigate(roleRoute)
                        }
                    }
                }
            }
        }

        // Pantallas sin MainScaffold
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(
            route = Screen.EditarUser.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            RegisterScreen(userId = userId, onNavigate = { navController.navigate(it) })
        }


        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigate = { roleRoute ->
                    navController.navigate(roleRoute) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ResetPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }

    }
}
