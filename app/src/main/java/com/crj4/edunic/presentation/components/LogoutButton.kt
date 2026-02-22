package com.crj4.edunic.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.crj4.edunic.presentation.viewmodel.AuthViewModel

@Composable
fun LogoutButton(
    viewModel: AuthViewModel,
    navController: NavHostController,
    currentRoute: String
) {
    Button(
        onClick = {
            viewModel.logout()
            navController.navigate("login") {
                popUpTo(currentRoute) { inclusive = true }
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onError)
    }
}
