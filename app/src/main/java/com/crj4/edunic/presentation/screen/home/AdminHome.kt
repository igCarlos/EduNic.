package com.crj4.edunic.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.crj4.edunic.presentation.viewmodel.AuthViewModel

@Composable
fun AdminHome(
    viewModel: AuthViewModel,
    navController: NavHostController,
) {

   Box(modifier = Modifier
       .fillMaxSize()
       .background(
           Brush.verticalGradient(
               colors = listOf(Color(0xFFBB86FC), Color(0xFF6200EE))
           )
       )){
       Column(
           modifier = Modifier
               .fillMaxSize()
               .padding(16.dp),
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Text(
               text = "Bienvenido Admin",
               style = MaterialTheme.typography.headlineMedium
           )


       }
   }
}
