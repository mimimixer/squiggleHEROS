package com.example.squiggleheros.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.squiggleheros.R
import com.example.squiggleheros.composables.SimpleBottomAppBar
import com.example.squiggleheros.composables.SimpleTopAppBar
import com.example.squiggleheros.navigation.DETAIL_SCREEN_KEY
import com.example.squiggleheros.navigation.Screen
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun GalleryScreen(navController: NavController) {
    val context = LocalContext.current
    var images by remember { mutableStateOf(loadImagesFromDirectory(context)) }
    var imageToDelete by remember { mutableStateOf<File?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SimpleTopAppBar(ContextCompat.getString(LocalContext.current, R.string.app_name), true, navController)
        },
        /*bottomBar = {
            SimpleBottomAppBar(navController)
        }*/
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                itemsIndexed(images) { index, imageFile ->
                    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable {
                                val encodedPath = URLEncoder.encode(imageFile.absolutePath, StandardCharsets.UTF_8.toString())
                                navController.navigate(Screen.Detail.route.replace("{$DETAIL_SCREEN_KEY}", encodedPath))
                            }
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = {
                                imageToDelete = imageFile
                                showDialog = true
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete), // Add your delete icon resource
                                contentDescription = "Delete",
                                tint = Color.Red // Adding a bright color for the delete icon
                            )
                        }
                    }
                }
            }
        }

        if (showDialog && imageToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Delete Image") },
                text = { Text(text = "Are you sure you want to delete this image?") },
                confirmButton = {
                    Button(
                        onClick = {
                            deleteImage(context, imageToDelete!!)
                            images = loadImagesFromDirectory(context)
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(Color.Red) // Adding a bright color for the delete button
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

fun loadImagesFromDirectory(context: Context): List<File> {
    val directory = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_PICTURES)[0]
    return directory.listFiles()?.filter { it.isFile && it.extension == "png" } ?: emptyList()
}

fun deleteImage(context: Context, file: File) {
    if (file.delete()) {
        Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show()
    }
}
