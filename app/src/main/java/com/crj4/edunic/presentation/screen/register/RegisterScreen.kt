package com.crj4.edunic.presentation.screen.register

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.crj4.edunic.domain.model.Role
import com.crj4.edunic.domain.model.User
import com.crj4.edunic.presentation.navigation.Screen
import com.crj4.edunic.presentation.viewmodel.AuthState
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    userId: String? = null,
    viewModel: AuthViewModel = viewModel(),
    onNavigate: (String) -> Unit
) {

    val context = LocalContext.current
    val state = viewModel.authState

    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(Role.STUDENT) }

    var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageBase64 by remember { mutableStateOf<String?>(null) }
    var imageError by remember { mutableStateOf("") }

    val maxImageSizeBytes = 150_000 // 150 KB
    val allowedTypes = listOf("image/jpeg", "image/jpg", "image/png")

    var formCompleted by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Cargar usuario si es edición
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadUserById(it) }
    }

    val user by viewModel.selectedUser.collectAsState()

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            lastname = it.lastname
            email = it.email
            selectedRole = Role.valueOf(it.role)
            selectedDateMillis = it.dateOfBirth?.toDate()?.time
            imageBase64 = it.image
            // Aquí puedes decodificar la imagen si quieres mostrar preview
        }
    }

    // Selector de imagen con validaciones
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val mimeType = context.contentResolver.getType(uri)
                if (mimeType !in allowedTypes) {
                    imageError = "Formato no permitido. Solo JPG, JPEG o PNG"
                    selectedBitmap = null
                    imageBase64 = null
                    return@rememberLauncherForActivityResult
                }

                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }

                val stream = ByteArrayOutputStream()
                if (mimeType == "image/png") {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                }

                val bytes = stream.toByteArray()
                if (bytes.size > maxImageSizeBytes) {
                    imageError = "La imagen supera ${maxImageSizeBytes / 1024} KB"
                    selectedBitmap = null
                    imageBase64 = null
                    return@rememberLauncherForActivityResult
                }

                imageBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                selectedBitmap = bitmap
                imageError = ""
                imageUri = uri

            } catch (e: Exception) {
                imageError = "Error al procesar la imagen"
                selectedBitmap = null
                imageBase64 = null
            }
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) =
                utcTimeMillis <= System.currentTimeMillis()
        }
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = if (userId == null) "Crear Usuario" else "Editar Usuario",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))

            // Avatar con preview y FAB para seleccionar imagen
            Box(contentAlignment = Alignment.BottomEnd) {

                if (selectedBitmap != null) {
                    Image(
                        bitmap = selectedBitmap!!.asImageBitmap(),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(
                            imageUri ?: if (imageBase64 != null)
                                "data:image/jpeg;base64,$imageBase64"
                            else null
                        ),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }

                FloatingActionButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }

            if (imageError.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(imageError, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = lastname,
                        onValueChange = { lastname = it },
                        label = { Text("Apellido") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            selectedDateMillis?.let {
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .format(Date(it))
                            } ?: "Seleccionar fecha de nacimiento"
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (userId == null) {
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Contraseña") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    if (passwordError.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(passwordError, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(Modifier.height(24.dp))

                    Text("Rol del Usuario", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))

                    Role.values().forEach { role ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedRole == role,
                                onClick = { selectedRole = role },
                                enabled = viewModel.currentRole == Role.ADMIN
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(role.name)
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            // Validaciones generales
                            if (name.isBlank() || lastname.isBlank() || email.isBlank() ||
                                (userId == null && password.isBlank())
                            ) {
                                passwordError = "Todos los campos son obligatorios"
                                return@Button
                            }

                            if (selectedDateMillis == null) {
                                passwordError = "Selecciona tu fecha de nacimiento"
                                return@Button
                            }

                            if (userId == null) {
                                val regex =
                                    Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{12,}$")
                                if (!regex.matches(password)) {
                                    passwordError =
                                        "Mínimo 12 caracteres, mayúscula, minúscula, número y símbolo"
                                    return@Button
                                }
                            }

                            if (imageBase64.isNullOrBlank()) {
                                imageError = "Debes seleccionar una imagen válida"
                                return@Button
                            }

                            passwordError = ""
                            imageError = ""

                            val timestamp = Timestamp(Date(selectedDateMillis!!))

                            if (userId == null) {
                                viewModel.register(
                                    name, lastname, timestamp,
                                    imageBase64 ?: "",
                                    email, password, selectedRole
                                ) {}
                            } else {
                                val updatedUser = User(
                                    uid = userId,
                                    name = name,
                                    lastname = lastname,
                                    email = email,
                                    role = selectedRole.toString(),
                                    dateOfBirth = timestamp,
                                    image = imageBase64 ?: ""
                                )
                                viewModel.updateUser(updatedUser)
                            }

                            formCompleted = true
                            Toast.makeText(
                                context,
                                if (userId == null) "Registrando..." else "Actualizando...",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(if (userId == null) "Registrar Usuario" else "Actualizar Usuario")
                    }

                    Spacer(Modifier.height(20.dp))

                    when (state) {
                        is AuthState.Loading -> CircularProgressIndicator()
                        is AuthState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                        else -> {}
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (formCompleted) {
            LaunchedEffect(Unit) {
                onNavigate(Screen.AdminHome.route)
            }
        }
    }
}