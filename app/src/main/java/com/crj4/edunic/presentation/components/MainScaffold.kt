package com.crj4.edunic.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavHostController
import com.crj4.edunic.domain.model.Role
import com.crj4.edunic.presentation.navigation.Screen
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    currentRoute: String?,
    bottomNavItems: List<BottomNavItem>,
    content: @Composable (PaddingValues) -> Unit
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(280.dp)
            ) {

                // 🔷 HEADER MODERNO
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EduNic",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Sistema Académico",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Divider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 🟢 INICIO
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    selected = currentRoute in listOf(
                        Screen.AdminHome.route,
                        Screen.TutorHome.route,
                        Screen.StudentHome.route
                    ),
                    onClick = {
                        when (authViewModel.currentRole) {
                            Role.ADMIN -> navController.navigate(Screen.AdminHome.route)
                            Role.TUTOR -> navController.navigate(Screen.TutorHome.route)
                            Role.STUDENT -> navController.navigate(Screen.StudentHome.route)
                            null -> navController.navigate(Screen.Login.route)
                        }
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary
                    )
                )

                // 🟢 PERFIL
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    selected = currentRoute == Screen.UserProfile.route,
                    onClick = {
                        navController.navigate(Screen.UserProfile.route)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary
                    )
                )

                // 🟢 USUARIOS
                NavigationDrawerItem(
                    label = { Text("Usuarios") },
                    icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null) },
                    selected = currentRoute == Screen.UserHome.route,
                    onClick = {
                        navController.navigate(Screen.UserHome.route)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Divider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 🔴 CERRAR SESIÓN
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    selected = false,
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.error,
                        unselectedTextColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        }
    ) {

        Scaffold(

            // 🔵 TOP BAR MODERNO
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "EduNic",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },

            // 🟡 BOTTOM NAV (tu versión flotante)
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    items = bottomNavItems,
                    authViewModel = authViewModel
                )
            }

        ) { innerPadding ->
            content(innerPadding)
        }
    }
}