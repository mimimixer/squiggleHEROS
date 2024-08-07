package com.example.squiggleheros.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Environment
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.squiggleheros.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import com.example.squiggleheros.composables.PaintView
import com.example.squiggleheros.composables.SimpleTopAppBarCanvas
import com.example.squiggleheros.navigation.Screen
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

@Composable
fun PaintViewComposable(
    brushColor: Int,
    brushSize: Float,
    eraserSize: Float,
    isEraserActive: Boolean,
    paintView: PaintView,
    onDrawingChange: () -> Unit
) {

    AndroidView(
        factory = {
            paintView.apply {
                this.brushColor = brushColor
                this.brushSize = brushSize
                this.eraserSize = eraserSize
                this.isEraserActive = isEraserActive
                this.onDrawingChange = onDrawingChange

            }
        },
        update = { view ->
            view.brushColor = brushColor
            view.brushSize = brushSize
            view.eraserSize = eraserSize
            view.isEraserActive = isEraserActive
        }
    )
}

@DrawableRes
fun getBrushIcon(brushSize: Float): Int {
    return when (brushSize) {
        16f -> R.drawable.brush_small
        32f -> R.drawable.brush_medium
        48f -> R.drawable.brush_large
        else -> R.drawable.draw
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CanvasScreen(navController: NavController, imagePath: String?) {
    val context = LocalContext.current
    val paintView = remember { PaintView(context) }
    val (currentBrushColor, setCurrentBrushColor) = remember { mutableStateOf(Color.BLACK) }
    val (currentBrushSize, setCurrentBrushSize) = remember { mutableStateOf(32f) }
    val (currentEraserSize, setCurrentEraserSize) = remember { mutableStateOf(16f) }
    val (isEraserActive, setIsEraserActive) = remember { mutableStateOf(false) }
    val (backgroundColor, setBackgroundColor) = remember { mutableStateOf(Color.WHITE) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    val pendingNavigationRoute by remember { mutableStateOf<String?>(null) }
    var showNewDrawingDialog by remember { mutableStateOf(false) }
    var showEraserSizeLabel: Int
    if (currentEraserSize == 32f){
        showEraserSizeLabel=2
    } else if (currentEraserSize == 48f){
        showEraserSizeLabel=3
    } else{
        showEraserSizeLabel=1
    }

    var navigationTarget by remember { mutableStateOf("") }

    fun changeBrushSize() {
        val sizes = listOf(16f, 32f, 48f)
        val newSize = sizes[(sizes.indexOf(currentBrushSize) + 1) % sizes.size]
        setCurrentBrushSize(newSize)
    }

// Load the image if an imagePath is provided
    LaunchedEffect(imagePath) {
        imagePath?.let {
            val bitmap = BitmapFactory.decodeFile(it)
            paintView.loadBitmap(bitmap)
        }
    }


    Scaffold(
        topBar = {
            SimpleTopAppBarCanvas(getString(LocalContext.current, R.string.app_name),
                onSaveClick = {
                    saveDrawing(context, paintView.getBitmap())
                    hasUnsavedChanges = false
                    Toast.makeText(context, R.string.drawing_saved, Toast.LENGTH_SHORT).show()
            },
                onNewDrawingClick = {
                    if (hasUnsavedChanges) {
                        navigationTarget = "canvas"
                        showNewDrawingDialog = true
                    } else {
                        navController.navigate(Screen.Canvas.route)
                    }
                },
                onGalleryClick = {
                    if (hasUnsavedChanges) {
                        navigationTarget = "gallery"
                        showNewDrawingDialog = true
                    } else {
                        navController.navigate(Screen.Gallery.route)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                //brush
                NavigationBarItem(
                    label={Text(getString(context, R.string.brush))}, //Text(currentBrushSize.toString())},
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.draw),
                            contentDescription = R.string.brush.toString(),
                            Modifier.size(30.dp),
                            tint = androidx.compose.ui.graphics.Color(currentBrushColor) //colorResource(id = R.color.Orange)
                        )
                    },
                    selected = !isEraserActive,
                    onClick = {
                        setIsEraserActive(false)
                    }
                )
                //brushsizechange
                NavigationBarItem(
                    label={Text(getString(context, R.string.brushsize))}, //Text(currentBrushSize.toString())},
                    icon = {
                        Icon(
                            painterResource(id = getBrushIcon(currentBrushSize)),
                            contentDescription = R.string.brushsize.toString(),
                            Modifier.size(30.dp),
                            tint = androidx.compose.ui.graphics.Color(currentBrushColor) //colorResource(id = R.color.Orange)
                        )
                    },
                    selected = !isEraserActive,
                    onClick = {
                        setIsEraserActive(false)
                        changeBrushSize()
                    }
                )
                //brush color change
                NavigationBarItem(
                    label = { Text(getString(context, R.string.color))},//Text(colorToName(currentBrushColor)) },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.color_picker),
                            contentDescription = R.string.color.toString(),
                            Modifier.size(30.dp),
                            tint = androidx.compose.ui.graphics.Color(currentBrushColor)
                        )
                    },
                    selected = !isEraserActive,
                    onClick = {
                        setIsEraserActive(false)
                        val colors =
                            listOf(
                                Color.BLACK,
                                Color.RED,
                                Color.MAGENTA,
                                Color.BLUE,
                                Color.GREEN,
                                Color.YELLOW,
                                Color.WHITE,
                                Color.CYAN
                            )
                        val newColor = colors[(colors.indexOf(currentBrushColor) + 1) % colors.size]
                        setCurrentBrushColor(newColor)
                    }
                )
                //eraser
                NavigationBarItem(
                    label = { Text("${getString(context, R.string.eraser)}${showEraserSizeLabel}")},
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.eraser),
                            contentDescription = R.string.eraser.toString(),
                            Modifier.size(30.dp),
                            tint = androidx.compose.ui.graphics.Color(backgroundColor)

                        )
                    },
                    selected = isEraserActive,
                    onClick = {
                        setIsEraserActive(true)
                        val sizes = listOf(32f, 48f, 16f)
                        setCurrentEraserSize(sizes[(sizes.indexOf(currentEraserSize) + 1) % sizes.size])
                    }
                )
                //change background color
                NavigationBarItem(
                    label = { Text(getString(context, R.string.background_color)) },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.baseline_format_paint_24), //background),
                            contentDescription = getString(context, R.string.background_color),
                            Modifier.size(30.dp),
                            tint = androidx.compose.ui.graphics.Color(backgroundColor)
                        )
                    },
                    selected = false,
                    onClick = {
                        val colors = listOf(
                            Color.WHITE,
                            Color.YELLOW,
                            Color.CYAN,
                            Color.BLUE,
                            Color.GREEN,
                            Color.MAGENTA,
                            Color.DKGRAY,
                            Color.LTGRAY
                        )
                        val newColor = colors[(colors.indexOf(backgroundColor) + 1) % colors.size]

                        if (currentBrushColor == newColor) {
                            setCurrentBrushColor(colors[(colors.indexOf(currentBrushColor) + 1) % colors.size])
                            val text = R.string.brushcolor_changed
                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                        }

                        setBackgroundColor(newColor)
                        paintView.setBackgroundColor(newColor)
                    }
                )
                //undo button
                NavigationBarItem(
                    label = { Text(getString(context, R.string.undo)) },
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_undo),
                            contentDescription = getString(context, R.string.undo),
                            Modifier.size(30.dp),
                            tint = colorResource(id = R.color.MediumSlateBlue)
                        )
                    },
                    selected = false,
                    onClick = {
                        paintView.undo()
                        val text = R.string.undo_last
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    ) {
        PaintViewComposable(
            brushColor = currentBrushColor,
            brushSize = currentBrushSize,
            eraserSize = currentEraserSize,
            isEraserActive = isEraserActive,
            paintView = paintView,
            onDrawingChange = { hasUnsavedChanges = true }
        )
    }

        
    if (showSaveDialog) {
        UnsavedChangesDialog(
            onSave = {
                saveDrawing(context, paintView.getBitmap())
                hasUnsavedChanges = false
                pendingNavigationRoute?.let { navController.navigate(it) }
                showSaveDialog = false
            },
            onDiscard = {
                hasUnsavedChanges = false
                pendingNavigationRoute?.let { navController.navigate(it) }
                showSaveDialog = false
            },
            onCancel = {
                showSaveDialog = false
            }
        )
    }

    if (showNewDrawingDialog) {
        UnsavedChangesDialog(
            onSave = {
                saveDrawing(context, paintView.getBitmap())
                hasUnsavedChanges = false
                if (navigationTarget == "canvas") {
                    navController.navigate(Screen.Canvas.route)
                } else if (navigationTarget == "gallery") {
                    navController.navigate(Screen.Gallery.route)
                }
                showNewDrawingDialog = false
            },
            onDiscard = {
                hasUnsavedChanges = false
                if (navigationTarget == "canvas") {
                    navController.navigate(Screen.Canvas.route)
                } else if (navigationTarget == "gallery") {
                    navController.navigate(Screen.Gallery.route)
                }
                showNewDrawingDialog = false
            },
            onCancel = {
                showNewDrawingDialog = false
            }
        )
    }
}



@Composable
fun UnsavedChangesDialog(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(getString(context, R.string.save)) },
        text = { Text(getString(context, R.string.unsaved_changes_text))},
        confirmButton = {
            Button(onClick = onSave) {
                Text(getString(context, R.string.save))
            }
        },
        dismissButton = {
            Button(onClick = onDiscard,) {
                Text(getString(context, R.string.discard))

            }
        },
        containerColor = colorResource(id = R.color.PowderBlue),
        titleContentColor = colorResource(id = R.color.DarkBlue),
        textContentColor = colorResource(id = R.color.Blue)
    )
}
fun saveDrawing(context: Context, bitmap: Bitmap) {
    val fileName = "drawing_${UUID.randomUUID()}.png"
    val directory = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_PICTURES)[0]
    val file = File(directory, fileName)
    val message = getString(context, R.string.unsaved_changes)+ file.absolutePath
    try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, R.string.failed_to_save, Toast.LENGTH_SHORT).show()
    }
}
