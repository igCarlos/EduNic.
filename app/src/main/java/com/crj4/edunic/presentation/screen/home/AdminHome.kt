package com.crj4.edunic.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.crj4.edunic.presentation.viewmodel.AuthViewModel

@Composable
fun AdminHome(
    viewModel: AuthViewModel,
    navController: NavHostController,
) {

    // 🔥 Datos simulados (puedes conectarlos luego)
    val totalTeachers = 12
    val totalStudents = 240
    val totalSubjects = 18
    val approvedStudents = 210

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E1E2C), Color(0xFF2A2A40))
                )
            )
            .padding(20.dp)
    ) {

        // 🔹 HEADER
        Text(
            text = "Bienvenido Admin",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Panel de control general del sistema",
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 🔹 KPI CARDS
        Text(
            text = "Resumen General",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardCard("Profesores", totalTeachers)
            DashboardCard("Estudiantes", totalStudents)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardCard("Materias", totalSubjects)
            DashboardCard("Aprobados", approvedStudents)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardCard("Estudiantes", totalSubjects)
            DashboardCard("Registrados", approvedStudents)
        }

        Spacer(modifier = Modifier.height(30.dp))


    }
}

// ---------------- TARJETA KPI ----------------

@Composable
fun DashboardCard(title: String, value: Int) {
    Card(
        modifier = Modifier
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A3A55)
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value.toString(),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ---------------- ACTIVIDAD ----------------

@Composable
fun ActivityItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF34344A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            color = Color.White
        )
    }
}