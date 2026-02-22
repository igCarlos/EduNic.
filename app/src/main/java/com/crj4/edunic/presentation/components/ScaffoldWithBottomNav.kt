package com.crj4.edunic.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.crj4.edunic.presentation.viewmodel.AuthViewModel

@Composable
fun ScaffoldWithBottomNav(
    navController: NavHostController,
    currentRoute: String,
    bottomNavItems: List<BottomNavItem>,
    content: @Composable (modifier: Modifier) -> Unit,
    authViewModel: AuthViewModel,
) {
    androidx.compose.material3.Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = bottomNavItems, authViewModel)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            content(Modifier)
        }
    }
}
