package com.crj4.edunic.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crj4.edunic.R
import com.crj4.edunic.domain.model.Role
import com.crj4.edunic.presentation.components.AppTextField
import com.crj4.edunic.presentation.navigation.Screen
import com.crj4.edunic.presentation.viewmodel.AuthState
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigate: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {

    val state = viewModel.authState

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
            ,
            contentAlignment = Alignment.Center
        ) {

            Card(
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .shadow(12.dp, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bienvenido a EduNic",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Inicia sesión para continuar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AppTextField(
                        email,
                        { email = it },
                        "Correo electrónico",
                        false,
                        Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        password,
                        { password = it },
                        "Contraseña",
                        true,
                        Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {

                            if (email.isBlank() || password.isBlank()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Los campos deben ser completados")
                                }
                                return@Button
                            }

                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Ingresa un correo válido")
                                }
                                return@Button
                            }

                            isSending = true
                            viewModel.login(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isSending
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Ingresar")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            onNavigate(Screen.ResetPassword.route)
                        }
                    ) {
                        Text("¿Olvidaste tu contraseña?")
                    }

//                    TextButton(
//                        onClick = onNavigateToRegister
//                    ) {
//                        Text("¿No tienes cuenta? Regístrate")
//                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        when (state) {

            is AuthState.Authenticated -> {
                LaunchedEffect(state) {
                    isSending = false
                    when (state.role) {
                        Role.ADMIN -> onNavigate("admin_home")
                        Role.TUTOR -> onNavigate("tutor_home")
                        Role.STUDENT -> onNavigate("student_home")
                    }
                }
            }

            is AuthState.Error -> {
                LaunchedEffect(state) {
                    isSending = false
                    snackbarHostState.showSnackbar(state.message)
                }
            }

            AuthState.Unauthenticated -> {
                isSending = false
            }

            else -> {}
        }
    }
}