package com.crj4.edunic.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SimpleList(items: List<String>, onItemClick: (String) -> Unit) {
    LazyColumn {
        items(items) { item ->
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) },
                headlineContent = { Text(item) }
            )
            Divider()
        }
    }
}
