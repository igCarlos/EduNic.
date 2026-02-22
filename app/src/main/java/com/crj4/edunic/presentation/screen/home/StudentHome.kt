package com.crj4.edunic.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.crj4.edunic.presentation.viewmodel.AuthViewModel

@Composable
fun StudentHome(
    viewModel: AuthViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido Estudiante", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Mis Cursos
        Button(onClick = { navController.navigate("courses") }) {
            Text("Mis Cursos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Mis Calificaciones
        Button(onClick = { navController.navigate("grades") }) {
            Text("Mis Calificaciones")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🔹 Botón Cerrar Sesión
        Button(
            onClick = {
                viewModel.logout()  // Cerramos sesión
                navController.navigate("login") {
                    popUpTo("student_home") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onError)
        }
    }
}
