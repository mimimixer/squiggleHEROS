package com.example.squiggleheros.screens


import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.squiggleheros.R
import com.example.squiggleheros.composables.SimpleBottomAppBar
import com.example.squiggleheros.composables.SimpleBottomAppBar2
import com.example.squiggleheros.composables.SimpleTopAppBar
import com.example.squiggleheros.navigation.DETAIL_SCREEN_KEY
import com.example.squiggleheros.navigation.Screen
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DetailScreen(navController: NavController, imagePath: String) {
    val context = LocalContext.current
    var file by remember { mutableStateOf(File(imagePath)) }
    var fileName by remember { mutableStateOf(TextFieldValue(file.name)) }
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SimpleTopAppBar(ContextCompat.getString(LocalContext.current, R.string.app_name), true, navController)
        },
        bottomBar = {
            SimpleBottomAppBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(16.dp)
                )
            } ?: run {
                Text(text = "Image not found", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = fileName,
                onValueChange = { fileName = it },
                label = { Text("Filename") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val newFile = File(file.parent, "${fileName.text}.${file.extension}")
                    if (file.renameTo(newFile)) {
                        file = newFile
                        navController.popBackStack()
                        navController.navigate("gallery_screen")
                    } else {
                        Toast.makeText(context, "Failed to rename file", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Save Filename")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    showDeleteDialog = true
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Delete Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val encodedPath = URLEncoder.encode(file.absolutePath, StandardCharsets.UTF_8.toString())
                    navController.navigate(Screen.Canvas.route.replace("{$DETAIL_SCREEN_KEY}", encodedPath))
                }
            ) {
                Text("Edit Image")
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Image") },
                    text = { Text("Are you sure you want to delete this image?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (file.exists() && file.delete()) {
                                    Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show()
                                }
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}