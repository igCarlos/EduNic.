package com.crj4.edunic.presentation.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ShowSnackBar(
    snackbarHostState: SnackbarHostState,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    LaunchedEffect(message) {
        snackbarHostState.showSnackbar(
            message = message,
            duration = duration
        )
    }
}
