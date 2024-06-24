package com.example.squiggleheros.screens

import android.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.squiggleheros.Greeting
import com.example.squiggleheros.R
import com.example.squiggleheros.composables.SimpleBottomAppBar
import com.example.squiggleheros.composables.SimpleTopAppBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.squiggleheros.composables.PaintView
import com.example.squiggleheros.composables.SimpleTopAppBarCanvas

@Composable
fun PaintViewComposable(
    brushColor: Int,
    brushSize: Float,
    eraserSize: Float,
    isEraserActive: Boolean
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            PaintView(ctx).apply {
                this.brushColor = brushColor
                this.brushSize = brushSize
                this.eraserSize = eraserSize
                this.isEraserActive = isEraserActive
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

@Composable
fun CanvasScreen(navController: NavController) {
    val (currentBrushColor, setCurrentBrushColor) = remember { mutableStateOf(Color.BLACK) }
    val (currentBrushSize, setCurrentBrushSize) = remember { mutableStateOf(8f) }
    val (currentEraserSize, setCurrentEraserSize) = remember { mutableStateOf(8f) }
    val (isEraserActive, setIsEraserActive) = remember { mutableStateOf(false) }
    val (savedBrushColor, setSavedBrushColor) = remember { mutableStateOf(currentBrushColor) }
    val (savedBrushSize, setSavedBrushSize) = remember { mutableStateOf(currentBrushSize) }

    fun changeBrushSize() {
        val sizes = listOf(8f, 24f, 32f)
        val newSize = sizes[(sizes.indexOf(currentBrushSize) + 1) % sizes.size]
        setCurrentBrushSize(newSize)
        setSavedBrushSize(newSize)
    }

    Scaffold(
        topBar = {
            SimpleTopAppBarCanvas("Canvas", true, navController)
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.brush_size), contentDescription = "Brush") },
                    selected = !isEraserActive,
                    onClick = {
                        setIsEraserActive(false)
                        setCurrentBrushColor(savedBrushColor)
                        setCurrentBrushSize(savedBrushSize)
                        changeBrushSize()
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.color_picker), contentDescription = "Color") },
                    selected = !isEraserActive,
                    onClick = {
                        setIsEraserActive(false)
                        val colors = listOf(Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
                        val newColor = colors[(colors.indexOf(currentBrushColor) + 1) % colors.size]
                        setCurrentBrushColor(newColor)
                        setSavedBrushColor(newColor)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(id = R.drawable.eraser), contentDescription = "Eraser") },
                    selected = isEraserActive,
                    onClick = {
                        setIsEraserActive(true)
                        val sizes = listOf(8f, 24f, 30f)
                        setCurrentEraserSize(sizes[(sizes.indexOf(currentEraserSize) + 1) % sizes.size])
                        setSavedBrushSize(currentBrushSize)
                        setSavedBrushColor(currentBrushColor)
                    }
                )
            }
        }
    ) {
        PaintViewComposable(
            brushColor = currentBrushColor,
            brushSize = currentBrushSize,
            eraserSize = currentEraserSize,
            isEraserActive = isEraserActive
        )
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
