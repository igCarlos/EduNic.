package com.crj4.edunic.presentation.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.ByteArrayOutputStream

@Composable
fun ImagePickerComponent(
    modifier: Modifier = Modifier,
    maxImageSizeBytes: Int = 150_000,
    allowedTypes: List<String> = listOf("image/jpeg", "image/jpg", "image/png"),
    onImageSelected: (base64: String) -> Unit
) {

    val context = LocalContext.current

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {
            try {

                val mimeType = context.contentResolver.getType(uri)
                if (mimeType !in allowedTypes) {
                    errorMessage = "Formato no permitido. Solo JPG, JPEG o PNG."
                    selectedBitmap = null
                    onImageSelected("")
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

                if (mimeType == "image/png") {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteStream)
                }

                val bytes = byteStream.toByteArray()

                if (bytes.size > maxImageSizeBytes) {
                    errorMessage = "La imagen supera ${maxImageSizeBytes / 1024} KB"
                    selectedBitmap = null
                    onImageSelected("")
                    return@rememberLauncherForActivityResult
                }

                val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)

                selectedBitmap = bitmap
                errorMessage = ""
                onImageSelected(base64)

            } catch (e: Exception) {
                errorMessage = "Error al procesar imagen"
                onImageSelected("")
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
    }
}