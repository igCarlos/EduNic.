package com.crj4.edunic.presentation.screen.profile

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: AuthViewModel = viewModel(),
    navController: NavHostController,
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val uid = viewModel.getCurrentUser()
        if (uid == null) {
            error = "Usuario no autenticado"
            isLoading = false
            return@LaunchedEffect
        }
        val result = viewModel.getUserData(uid)
        if (result.isSuccess) {
            userData = result.getOrNull()
        } else {
            error = result.exceptionOrNull()?.localizedMessage ?: "Error desconocido"
        }
        isLoading = false
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF6200EE))
        }
        return
    }

    if (error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(error!!, color = Color.Red)
        }
        return
    }

    userData?.let { data ->
        val name = data["name"] as? String ?: ""
        val lastname = data["lastname"] as? String ?: ""
        val email = data["email"] as? String ?: ""
        val dob = data["dateOfBirth"] as? Timestamp
        val role = data["role"] as? String ?: "user"
        val imageBase64 = data["image"] as? String

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Imagen de perfil con borde y sombra
                val bitmapImage = remember(imageBase64) {
                    imageBase64?.let {
                        val bytes = Base64.decode(it, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                }
                bitmapImage?.let { bmp ->
                    Card(
                        shape = RoundedCornerShape(100.dp),
                        elevation = CardDefaults.cardElevation(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.size(160.dp).padding(10.dp,10.dp)
                    ) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Imagen de perfil",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre completo destacado
                Text(
                    text = "$name $lastname",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF1E1E2C)
                )

                // Rol debajo del nombre
                Text(
                    text = role.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6200EE)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tarjeta de información profesional
                Card(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        InfoRow(label = "Email", value = email)
                        dob?.let {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            InfoRow(label = "Fecha de nacimiento", value = sdf.format(it.toDate()))
                        }
                        InfoRow(label = "Rol", value = role)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1E1E2C)
        )
    }
}
