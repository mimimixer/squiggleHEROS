package com.example.squiggleheros.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.squiggleheros.R
import com.example.squiggleheros.composables.SimpleAppTopBarDetail
import com.example.squiggleheros.navigation.Screen
import java.io.File
import java.io.IOException
import java.io.OutputStream
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
            SimpleAppTopBarDetail(
                ContextCompat.getString(LocalContext.current, R.string.app_name),
                onNewDrawingClick = {navController.navigate(Screen.Canvas.route)},
                onGalleryClick = {navController.popBackStack() })
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                Modifier.fillMaxHeight(0.72f)
            ){
                bitmap?.let {
                    DisplayImage(it.asImageBitmap())
                } ?: run {
                    Text(ContextCompat.getString(LocalContext.current, R.string.drawing_not_found), color = MaterialTheme.colorScheme.error)
                }
            }
            FileNameInput(fileName) { fileName = it }
            Row(modifier = Modifier
                .height(140.dp)
                .fillMaxWidth()
                .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){

                SaveIcon(context, file, fileName, navController)


                EditIcon(navController, file.absolutePath)

                DeleteIcon { showDeleteDialog = true }
            }


            if (showDeleteDialog) {
                DeleteDialog(
                    context,
                    file,
                    onDismiss = { showDeleteDialog = false },
                    onDeleteConfirmed = {
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun DisplayImage(bitmap: androidx.compose.ui.graphics.ImageBitmap) {
    val configurate = LocalConfiguration.current
    val screenHeight = configurate.screenHeightDp.dp
    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier = Modifier
            //.fillMaxWidth()
            //.aspectRatio(1f)
            .padding(5.dp)
            .height(screenHeight * 0.7f),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun FileNameInput(fileName: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = fileName,
        onValueChange = onValueChange,
        label = {ContextCompat.getString(LocalContext.current, R.string.filename) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SaveIcon(context: Context, file: File, fileName: TextFieldValue, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        IconButton(
            onClick = {
                val newFile = File(file.parent, "${fileName.text}")//.${file.extension}")
                if (file.renameTo(newFile)) {
                    saveImageToGallery(context, newFile)
                    val text = R.string.file_renamed
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                    navController.navigate(Screen.Gallery.route)
                } else {
                    val text = R.string.failed_to_rename
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Icon(painterResource(id = R.drawable.ic_save),
                contentDescription = "Save Filename",
                Modifier.size(100.dp),
                tint = colorResource(id = R.color.HotPink)
            )
        }
        Text(text = ContextCompat.getString(LocalContext.current, R.string.save))
    }
}


@Composable
fun DeleteIcon(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = onClick,
        ) {
            Icon(painterResource(id = R.drawable.ic_delete_forever),
                contentDescription = "Delete Image",
                Modifier.size(100.dp),
                tint = colorResource(id = R.color.CornflowerBlue)
            )
        }
        Text(text = ContextCompat.getString(LocalContext.current, R.string.delete))
    }
}

@Composable
fun EditIcon(navController: NavController, filePath: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = {
                val encodedPath = URLEncoder.encode(filePath, StandardCharsets.UTF_8.toString())
                navController.navigate("canvas_screen?imagePath=$encodedPath")
            }
        ) {
            Icon(painterResource(id = R.drawable.brush),
                contentDescription = "Edit Image",
                Modifier.size(100.dp),
                tint = colorResource(id = R.color.Orange)
            )
        }
        Text(text = ContextCompat.getString(LocalContext.current, R.string.edit))
    }
}

@Composable
fun DeleteDialog(
    context: Context,
    file: File,
    onDismiss: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(ContextCompat.getString(LocalContext.current, R.string.delete_drawing)) },
        text = {ContextCompat.getString(LocalContext.current, R.string.delete_ask) },
        confirmButton = {
            Button(
                onClick = {
                    if (file.exists() && file.delete()) {
                        val text = R.string.drawing_deleted
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                        onDeleteConfirmed()
                    } else {
                        val text = R.string.failed_to_delete
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.Tulip_Pink))
            ) {
                Text(ContextCompat.getString(LocalContext.current, R.string.delete ))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.purple_200))
            ) {
                Text(ContextCompat.getString(LocalContext.current, R.string.cancel))
            }
        }
    )
}

fun saveImageToGallery(context: Context, file: File) {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SquiggleHEROS")
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    uri?.let {
        try {
            val outputStream: OutputStream? = resolver.openOutputStream(it)
            outputStream?.use { out ->
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            val text = R.string.drawing_saved_to_gallery
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            val text = R.string.failed_to_save
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}
