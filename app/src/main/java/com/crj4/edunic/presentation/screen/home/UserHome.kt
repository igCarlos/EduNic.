package com.crj4.edunic.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.crj4.edunic.domain.manager.RoleManager
import com.crj4.edunic.domain.model.Permission
import com.crj4.edunic.presentation.components.Buscador
import com.crj4.edunic.presentation.components.MainButton
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import com.crj4.edunic.presentation.components.UserCard
import com.crj4.edunic.presentation.navigation.Screen

@Composable
fun UserHome(
    viewModel: AuthViewModel,
    navController: NavHostController
) {

//    val users by viewModel.usersRealTime.collectAsState()
    val users by viewModel.filteredUsers.collectAsState()
    val currentRole = viewModel.currentRole

    Box( modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                colors = listOf(Color(0xFFBB86FC), Color(0xFF6200EE))
            )
        )){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp,80.dp)
        ) {

            Text(
                text = "Bienvenido Usuario",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            Buscador(viewModel)

            Spacer(Modifier.height(8.dp))

            if (currentRole != null && RoleManager.hasPermission(currentRole, Permission.CREATE_USER)) {
                MainButton("Nuevo Usuario",onClick = { navController.navigate(Screen.RegisterUser.route) },Modifier.padding(2.dp))
            }
            if (users.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay usuarios registrados")
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {

                    items(users) { user ->
                        UserCard(navController,user = user,viewModel) {}
                    }
                }
            }
        }
    }

}
