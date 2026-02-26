package com.crj4.edunic.presentation.screen.materia

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.crj4.edunic.domain.model.Role
import com.crj4.edunic.domain.model.Subject
import com.crj4.edunic.domain.model.User
import com.crj4.edunic.presentation.components.ImagePickerComponent
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import com.crj4.edunic.presentation.viewmodel.SubjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubjectDialog(
    subject: Subject,
    subjectViewModel: SubjectViewModel,
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit
) {

    var name by remember { mutableStateOf(subject.name) }
    var gradeId by remember { mutableStateOf(subject.gradeId) }
    var gradeName by remember { mutableStateOf(subject.gradeName) }
    var isActive by remember { mutableStateOf(subject.isActive) }
    var imageBase64 by remember { mutableStateOf(subject.imageUrl) }

    val teachers by authViewModel.filteredUsers.collectAsState()
    val currentUser by authViewModel.currentUserData.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var selectedTeacher by remember {
        mutableStateOf<User?>(teachers.find { it.uid == subject.teacherId })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.secondary,
        title = { Text("Editar Materia") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la materia") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // 🔥 COMPONENTE DE IMAGEN (soporta imagen inicial)
                ImagePickerComponent(
                    modifier = Modifier.fillMaxWidth(),
                    onImageSelected = { base64 ->
                        imageBase64 = base64
                    }
                )

                // Dropdown Profesor
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        if (currentUser?.role == Role.ADMIN.name) {
                            expanded = !expanded
                        }
                    }
                ) {

                    OutlinedTextField(
                        value = selectedTeacher?.let { "${it.name} ${it.lastname}" }
                            ?: subject.teacherName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Profesor") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        enabled = currentUser?.role == Role.ADMIN.name
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        teachers.forEach { teacher ->
                            DropdownMenuItem(
                                text = { Text("${teacher.name} ${teacher.lastname}") },
                                onClick = {
                                    selectedTeacher = teacher
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // ID Grado
                OutlinedTextField(
                    value = gradeId,
                    onValueChange = { gradeId = it },
                    label = { Text("ID Grado") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Nombre Grado
                OutlinedTextField(
                    value = gradeName,
                    onValueChange = { gradeName = it },
                    label = { Text("Nombre Grado") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Switch Activo
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Materia Activa")
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    subjectViewModel.updateSubject(
                        subject.copy(
                            name = name,
                            imageUrl = imageBase64,
                            teacherId = selectedTeacher?.uid ?: subject.teacherId,
                            teacherName = selectedTeacher?.let {
                                "${it.name} ${it.lastname}"
                            } ?: subject.teacherName,
                            gradeId = gradeId,
                            gradeName = gradeName,
                            isActive = isActive
                        )
                    )
                    onDismiss()
                },
                enabled = name.isNotBlank() && imageBase64.isNotEmpty()
            ) {
                Text("Guardar")
            }
        },

        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },

        shape = RoundedCornerShape(16.dp)
    )
}