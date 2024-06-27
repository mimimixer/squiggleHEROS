package com.example.squiggleheros.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.squiggleheros.Greeting
import com.example.squiggleheros.R
import com.example.squiggleheros.composables.SimpleBottomAppBar
import com.example.squiggleheros.composables.SimpleTopAppBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.squiggleheros.composables.PaintView
import com.example.squiggleheros.composables.SimpleTopAppBarCanvas
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
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CanvasScreen(navController: NavController, imagePath: String?) {
    val context = LocalContext.current
    val paintView = remember { PaintView(context) }
    val (currentBrushColor, setCurrentBrushColor) = remember { mutableStateOf(Color.BLACK) }
    val (currentBrushSize, setCurrentBrushSize) = remember { mutableStateOf(8f) }
    val (currentEraserSize, setCurrentEraserSize) = remember { mutableStateOf(8f) }
    val (isEraserActive, setIsEraserActive) = remember { mutableStateOf(false) }
    val (savedBrushColor, setSavedBrushColor) = remember { mutableStateOf(currentBrushColor) }
    val (savedBrushSize, setSavedBrushSize) = remember { mutableStateOf(currentBrushSize) }
    val (backgroundColor, setBackgroundColor) = remember { mutableStateOf(Color.WHITE) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var pendingNavigationRoute by remember { mutableStateOf<String?>(null) }
    var showNewDrawingDialog by remember { mutableStateOf(false) }

    var navigationTarget by remember { mutableStateOf("") }

    fun changeBrushSize() {
        val sizes = listOf(8f, 24f, 32f)
        val newSize = sizes[(sizes.indexOf(currentBrushSize) + 1) % sizes.size]
        setCurrentBrushSize(newSize)
        setSavedBrushSize(newSize)
    }


    fun handleNavigation(route: String) {
        if (hasUnsavedChanges) {
            showSaveDialog = true
            pendingNavigationRoute = route
        } else {
            navController.navigate(route)
        }
    }




    Scaffold(
        topBar = {
            SimpleTopAppBarCanvas("SquiggleHeros", true, navController,
                onSaveClick = {
                    saveDrawing(context, paintView.getBitmap())
                    hasUnsavedChanges = false
                    Toast.makeText(context, "Drawing saved", Toast.LENGTH_SHORT).show()
            },
                onNewDrawingClick = {
                    if (hasUnsavedChanges) {
                        navigationTarget = "canvas"
                        showNewDrawingDialog = true
                    } else {
                        navController.navigate("canvas_screen")
                    }
                },
                onGalleryClick = {
                    if (hasUnsavedChanges) {
                        navigationTarget = "gallery"
                        showNewDrawingDialog = true
                    } else {
                        navController.navigate("gallery_screen")
                    }
                }
                )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.brush_size),
                            contentDescription = "Brush"
                        )
                    },
                    selected = !isEraserActive,
                    onClick = {
                        setIsEraserActive(false)
                        setCurrentBrushColor(savedBrushColor)
                        setCurrentBrushSize(savedBrushSize)
                        changeBrushSize()
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.color_picker),
                            contentDescription = "Color"
                        )
                    },
                    selected = !isEraserActive,
                    onClick = {
                        setIsEraserActive(false)
                        val colors =
                            listOf(Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
                        val newColor = colors[(colors.indexOf(currentBrushColor) + 1) % colors.size]
                        setCurrentBrushColor(newColor)
                        setSavedBrushColor(newColor)
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.eraser),
                            contentDescription = "Eraser"
                        )
                    },
                    selected = isEraserActive,
                    onClick = {
                        setIsEraserActive(true)
                        val sizes = listOf(8f, 24f, 30f)
                        setCurrentEraserSize(sizes[(sizes.indexOf(currentEraserSize) + 1) % sizes.size])
                        setSavedBrushSize(currentBrushSize)
                        setSavedBrushColor(currentBrushColor)
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.background),
                            contentDescription = "Background Color"
                        )
                    },
                    selected = false,
                    onClick = {
                        val colors = listOf(
                            Color.WHITE,
                            Color.LTGRAY,
                            Color.DKGRAY,
                            Color.YELLOW,
                            Color.CYAN,
                            Color.GREEN,
                            Color.MAGENTA
                        )
                        val newColor = colors[(colors.indexOf(backgroundColor) + 1) % colors.size]
                        setBackgroundColor(newColor)
                        paintView.setBackgroundColor(newColor)
                    }
                ) // Add undo button
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_undo),
                            contentDescription = "Undo"
                        )
                    },
                    selected = false,
                    onClick = {
                        paintView.undo()
                        Toast.makeText(context, "Undo last action", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    )  {
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
                    navController.navigate("canvas_screen")
                } else if (navigationTarget == "gallery") {
                    navController.navigate("gallery_screen")
                }
                showNewDrawingDialog = false
            },
            onDiscard = {
                hasUnsavedChanges = false
                if (navigationTarget == "canvas") {
                    navController.navigate("canvas_screen")
                } else if (navigationTarget == "gallery") {
                    navController.navigate("gallery_screen")
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
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Unsaved Changes") },
        text = { Text("You have unsaved changes. Do you want to save them before leaving?") },
        confirmButton = {
            Button(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDiscard) {
                Text("Discard")
            }
        }
    )
}
fun saveDrawing(context: Context, bitmap: Bitmap) {
    val fileName = "drawing_${UUID.randomUUID()}.png"
    val directory =
        ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_PICTURES)[0]
    val file = File(directory, fileName)

    try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            Toast.makeText(context, "Saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
    }
}





/*@Composable
fun CanvasScreen (navController: NavController){

    Scaffold(
        topBar = {
            SimpleTopAppBar(getString(LocalContext.current, R.string.app_name), false, navController)
        },
        bottomBar = {
            SimpleBottomAppBar(navController)
        }
    ) { values ->
        Greeting(
            name = getString(LocalContext.current, R.string.canvas_screen),
            paddingValues = values
        )
    }

}*/
