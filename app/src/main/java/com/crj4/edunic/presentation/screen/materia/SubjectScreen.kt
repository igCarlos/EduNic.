package com.crj4.edunic.presentation.screen.materia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crj4.edunic.domain.manager.RoleManager
import com.crj4.edunic.domain.model.Permission
import com.crj4.edunic.domain.model.Subject
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import com.crj4.edunic.presentation.viewmodel.SubjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(subjectViewModel: SubjectViewModel,authViewModel: AuthViewModel) {

    val subjects by subjectViewModel.subjects.collectAsState()
    val isLoading by subjectViewModel.isLoading.collectAsState()
    val errorMessage by subjectViewModel.errorMessage.collectAsState()
    val currentRole by subjectViewModel.currentRole.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFBB86FC), Color(0xFF6200EE))
                )
            )
            .padding(0.dp,100.dp)
    ) {

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // Título
            Text(
                text = "Materias",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            val currentRole = authViewModel.currentRole
            if (currentRole != null && RoleManager.hasPermission(currentRole, Permission.CREATE_SUBJECT)){
                // Botón Crear Materia
                Button(
                    onClick = { showCreateDialog = true },
                    enabled = subjectViewModel.can(Permission.CREATE_SUBJECT),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Crear Materia", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar error
            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Mostrar diálogo si showCreateDialog es true
            if (showCreateDialog) {
                CreateSubjectDialog(subjectViewModel,authViewModel, onDismiss = { showCreateDialog = false })
            }


            // Lista de materias
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjects) { subject ->
                    SubjectCard(subject, subjectViewModel, authViewModel)
                }
            }
        }

        // 🔹 Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
fun SubjectCard(subject: Subject, subjectViewModel: SubjectViewModel,authViewModel: AuthViewModel) {

    var showEditDialog by remember { mutableStateOf(false) }
    val currentRole = authViewModel.currentRole
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    text = subject.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = "Profesor: ${subject.teacherName}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Grado: ${subject.gradeName}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Row {

                Box(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    if (currentRole != null && RoleManager.hasPermission(currentRole, Permission.UPDATE_SUBJECT) || currentRole != null && RoleManager.hasPermission(currentRole, Permission.DELETE_SUBJECT))

                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = MaterialTheme.colorScheme.secondary

                        ) {
                            if (RoleManager.hasPermission(currentRole, Permission.UPDATE_SUBJECT)) {
                                DropdownMenuItem(
                                    text = { Text("Editar") },
                                    leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                                    onClick = {
                                        showEditDialog = true
                                    }
                                )
                            }

//                            IconButton(
//                                onClick = { showEditDialog = true },
//                                enabled = subjectViewModel.can(Permission.UPDATE_SUBJECT)
//                            ) {
//                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFFFFC107))
//                            }


                            if (RoleManager.hasPermission(currentRole, Permission.DELETE_SUBJECT)) {

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
                                        text = { Text("¿Estás seguro de eliminar a ${subject.name}?") },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                subjectViewModel.deleteSubject(subject.id)
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


//                if (currentRole != null && RoleManager.hasPermission(currentRole, Permission.DELETE_SUBJECT)){
//                    IconButton(
//                        onClick = { subjectViewModel.deleteSubject(subject.id) },
//                        enabled = subjectViewModel.can(Permission.DELETE_SUBJECT)
//                    ) {
//                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFF44336))
//                    }
//                }

            }
        }
    }

    // Mostrar diálogo de edición si showEditDialog es true
    if (showEditDialog) {
        EditSubjectDialog(
            subject = subject,
            subjectViewModel = subjectViewModel,
            authViewModel = authViewModel,
            onDismiss = { showEditDialog = false }
        )
    }
}