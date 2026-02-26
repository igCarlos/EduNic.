package com.crj4.edunic.presentation.screen.materia

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.crj4.edunic.domain.model.Role
import com.crj4.edunic.domain.model.Subject
import com.crj4.edunic.domain.model.User
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import com.crj4.edunic.presentation.viewmodel.SubjectViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSubjectDialog(
    subjectViewModel: SubjectViewModel,
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var teacherId by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    var gradeId by remember { mutableStateOf("") }
    var gradeName by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var imageBase64 by remember { mutableStateOf("") }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val maxImageSizeBytes = 150_000 // 150 KB
    val allowedTypes = listOf("image/jpeg", "image/jpg", "image/png")

    val teachers by authViewModel.filteredUsers.collectAsState()
    val currentUser by authViewModel.currentUserData.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var selectedTeacher by remember { mutableStateOf<User?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {
            try {

                val mimeType = context.contentResolver.getType(uri)
                if (mimeType !in allowedTypes) {
                    errorMessage = "Formato no permitido. Solo JPG, JPEG o PNG."
                    selectedBitmap = null
                    imageBase64 = ""
                    return@rememberLauncherForActivityResult
                }

                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    android.provider.MediaStore.Images.Media.getBitmap(
                        context.contentResolver,
                        uri
                    )
                }

                val byteStream = ByteArrayOutputStream()
                // Guardar como PNG si es PNG, si no JPEG
                if (mimeType == "image/png") {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteStream)
                }

                val bytes = byteStream.toByteArray()

                if (bytes.size > maxImageSizeBytes) {
                    errorMessage = "La imagen supera ${maxImageSizeBytes / 1024} KB"
                    selectedBitmap = null
                    imageBase64 = ""
                    return@rememberLauncherForActivityResult
                }

                imageBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                selectedBitmap = bitmap
                errorMessage = ""

            } catch (e: Exception) {
                errorMessage = "Error al procesar imagen"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.secondary,
        title = { Text("Crear Materia") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la materia") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // 🔥 PREVISUALIZACIÓN
                if (selectedBitmap != null) {
                    Image(
                        bitmap = selectedBitmap!!.asImageBitmap(),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (selectedBitmap == null)
                            "Seleccionar imagen"
                        else
                            "Cambiar imagen"
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        if (currentUser?.role == Role.ADMIN.name) {
                            expanded = !expanded
                        }
                    }
                ) {

                    OutlinedTextField(
                        value = selectedTeacher?.name ?: "",
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
                                text = {
                                    Text("${teacher.name} ${teacher.lastname}")
                                },
                                onClick = {
                                    selectedTeacher = teacher
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = gradeId,
                    onValueChange = { gradeId = it },
                    label = { Text("ID Grado") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = gradeName,
                    onValueChange = { gradeName = it },
                    label = { Text("Nombre Grado") },
                    modifier = Modifier.fillMaxWidth()
                )

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
                    subjectViewModel.saveSubject(
                        Subject(
                            name = name,
                            imageUrl = imageBase64,
                            teacherId = selectedTeacher?.uid?:"",
                            teacherName = "${selectedTeacher?.name} ${selectedTeacher?.lastname}",
                            gradeId = gradeId,
                            gradeName = gradeName,
                            isActive = isActive
                        )
                    )
                    onDismiss()
                },
                enabled = name.isNotBlank() && imageBase64.isNotEmpty()
            ) {
                Text("Crear")
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