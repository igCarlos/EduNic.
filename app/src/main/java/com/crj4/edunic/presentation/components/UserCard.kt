package com.crj4.edunic.presentation.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.crj4.edunic.domain.manager.RoleManager
import com.crj4.edunic.domain.model.Permission
import com.crj4.edunic.domain.model.User
import com.crj4.edunic.presentation.navigation.Screen
import com.crj4.edunic.presentation.viewmodel.AuthViewModel

@Composable
fun UserCard(
    navController: NavHostController,
    user: User,
    viewModel: AuthViewModel, // <- agregamos esto
    onClick: (User) -> Unit
) {

    val bitmap = remember(user.image) {
        base64ToBitmap(user.image)
    }
    val users by viewModel.usersRealTime.collectAsState()
    val currentRole = viewModel.currentRole

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick(user) }
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large,

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Imagen del usuario
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Imagen de ${user.name}",
                    contentScale = ContentScale.Crop, // 🔥 llena el círculo
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
            } else {
                // Imagen por defecto
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", style = MaterialTheme.typography.headlineMedium)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del usuario
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${user.name} ${user.lastname}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = user.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                if (currentRole != null && RoleManager.hasPermission(currentRole, Permission.UPDATE_USER) || currentRole != null &&  RoleManager.hasPermission(currentRole, Permission.DELETE_USER))

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = MaterialTheme.colorScheme.secondary

                ) {
                    if (currentRole != null && RoleManager.hasPermission(currentRole, Permission.UPDATE_USER)) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                            onClick = {
                                // Navegar a RegisterScreen para editar
                                navController.navigate("${Screen.RegisterUser.route}/${user.uid}")

                            }
                        )
                    }

                    if (currentRole != null &&  RoleManager.hasPermission(currentRole, Permission.DELETE_USER)) {

                        var showDeleteDialog by remember { mutableStateOf(false) }

                        DropdownMenuItem(
                            text = { Text("Eliminar") },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showDeleteDialog = true // mostrar diálogo de confirmación
                            }
                        )

                        // Diálogo de confirmación
                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                title = { Text("Confirmar eliminación") },
                                text = { Text("¿Estás seguro de eliminar a ${user.name}?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        viewModel.deleteUser(user.uid)  // Llamada al ViewModel
                                        showDeleteDialog = false
                                    }) {
                                        Text("Eliminar")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteDialog = false }) {
                                        Text("Cancelar")
                                    }
                                }
                            )
                        }

                    }


                }
            }
        }
    }
}

/**
 * Convierte Base64 en Bitmap
 */
fun base64ToBitmap(base64Str: String?): Bitmap? {
    if (base64Str.isNullOrBlank()) return null

    return try {

        // Elimina posible prefijo: data:image/png;base64,
        val cleanBase64 = base64Str
            .substringAfter("base64,", base64Str)
            .replace("\n", "")
            .replace(" ", "")

        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

