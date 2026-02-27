package com.crj4.edunic.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.crj4.edunic.domain.model.Role
import com.crj4.edunic.presentation.navigation.Screen
import com.crj4.edunic.presentation.viewmodel.AuthViewModel

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem>,
    authViewModel: AuthViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp), //  Separación del borde inferior
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth(0.92f) // No ocupa todo el ancho
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = item.label
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    label = { Text(item.label) },
                    selected = currentRoute == item.route,
                    onClick = {

                        val targetRoute = if (item.label == "Inicio") {
                            when (authViewModel.currentRole) {
                                Role.ADMIN -> Screen.AdminHome.route
                                Role.TUTOR -> Screen.AdminHome.route
                                Role.STUDENT -> Screen.AdminHome.route
                                null -> Screen.Login.route
                            }
                        } else item.route

                        if (currentRoute != targetRoute) {
                            navController.navigate(targetRoute) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}
