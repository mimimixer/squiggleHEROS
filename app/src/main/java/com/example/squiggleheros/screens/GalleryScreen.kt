package com.example.squiggleheros.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.squiggleheros.R
import com.example.squiggleheros.composables.SimpleAppTopBarGallery
import com.example.squiggleheros.navigation.DETAIL_SCREEN_KEY
import com.example.squiggleheros.navigation.Screen
import com.example.squiggleheros.utils.PreferenceManager
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun GalleryScreen(navController: NavController) {
    val context = LocalContext.current
    val images by remember { mutableStateOf(loadImagesFromDirectory(context)) }

    // Load favorite states from SharedPreferences
    var favorites by remember { mutableStateOf(PreferenceManager.loadFavorites(context)) }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
                 SimpleAppTopBarGallery(
                     title = ContextCompat.getString(LocalContext.current, R.string.app_name),
                     onNewDrawingClick = { navController.navigate(Screen.Canvas.route) },
                     onFilterFavoritesClick = { showFavoritesOnly = !showFavoritesOnly },
                     showFavoritesOnly = showFavoritesOnly
                 )
        },
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
                        if (!showFavoritesOnly) {
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
        }
    }
}

fun loadImagesFromDirectory(context: Context): List<File> {
    val directory = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_PICTURES)[0]
    return directory.listFiles()?.filter { it.isFile} ?: emptyList() //it.isFile && it.extension == "png"
}


