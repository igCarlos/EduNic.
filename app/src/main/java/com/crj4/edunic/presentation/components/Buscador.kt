package com.crj4.edunic.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crj4.edunic.presentation.viewmodel.AuthViewModel
import androidx.compose.material3.TextFieldDefaults
import com.crj4.edunic.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Buscador(viewModel: AuthViewModel) {
    val searchText by viewModel.searchQuery.collectAsState()

    TextField(
        value = searchText,
        onValueChange = { viewModel.onSearchChange(it) },
        placeholder = { Text("Buscar usuario...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))

    )
}