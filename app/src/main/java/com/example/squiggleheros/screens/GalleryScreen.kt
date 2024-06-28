package com.example.squiggleheros.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.squiggleheros.R
import com.example.squiggleheros.composables.SimpleTopAppBar
import com.example.squiggleheros.navigation.DETAIL_SCREEN_KEY
import com.example.squiggleheros.navigation.Screen
import com.example.squiggleheros.utils.PreferenceManager
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun GalleryScreen(navController: NavController) {
    val context = LocalContext.current
    var images by remember { mutableStateOf(loadImagesFromDirectory(context)) }

    // Load favorite states from SharedPreferences
    var favorites by remember { mutableStateOf(PreferenceManager.loadFavorites(context)) }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    var imageToDelete by remember { mutableStateOf<File?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SimpleTopAppBar(ContextCompat.getString(LocalContext.current, R.string.app_name), true, navController)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                Modifier
                    .clickable { showFavoritesOnly = !showFavoritesOnly }
                    //onClick = ,
                    .align(Alignment.CenterHorizontally)

                    .background(colorResource(id = R.color.Blossom_Pink))
                    .fillMaxWidth()
                    .height(30.dp),
                horizontalArrangement =Arrangement.Center,
                verticalAlignment =Alignment.CenterVertically

            ) {
                Text(if (showFavoritesOnly) "Show All" else "Show Favorites", Modifier.height(25.dp))
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                val displayedImages = if (showFavoritesOnly) images.filter { it.absolutePath in favorites } else images
                itemsIndexed(displayedImages) { _, imageFile ->
                    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                    Box(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable {
                                val encodedPath = URLEncoder.encode(
                                    imageFile.absolutePath,
                                    StandardCharsets.UTF_8.toString()
                                )
                                navController.navigate(
                                    Screen.Detail.route.replace(
                                        "{$DETAIL_SCREEN_KEY}",
                                        encodedPath
                                    )
                                )
                            }
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                        /*IconButton(
                            onClick = { imageToDelete = imageFile; showDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }*/
                        IconButton(
                            onClick = {
                                if (imageFile.absolutePath in favorites) {
                                    favorites = favorites - imageFile.absolutePath
                                } else {
                                    favorites = favorites + imageFile.absolutePath
                                }
                                PreferenceManager.saveFavorites(context, favorites)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = if (imageFile.absolutePath in favorites) R.drawable.ic_favorite else R.drawable.ic_favorite_border),
                                contentDescription = "Favorite",
                                tint = colorResource(id = R.color.Bean_Red)
                            )
                        }
                    }
                }
            }
        }

        if (showDialog && imageToDelete != null) {
            DeleteConfirmationDialog(
                context = context,
                imageToDelete = imageToDelete!!,
                onDismiss = { showDialog = false },
                onDeleteConfirmed = {
                    deleteImage(context, imageToDelete!!)
                    images = loadImagesFromDirectory(context)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    context: Context,
    imageToDelete: File,
    onDismiss: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete Image") },
        text = { Text(text = "Are you sure you want to delete this image?") },
        confirmButton = {
            Button(
                onClick = {
                    deleteImage(context, imageToDelete)
                    onDeleteConfirmed()
                },
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
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
