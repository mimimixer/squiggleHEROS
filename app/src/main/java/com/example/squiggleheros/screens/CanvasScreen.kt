package com.example.squiggleheros.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.squiggleheros.Greeting
import com.example.squiggleheros.R
import com.example.squiggleheros.composables.SimpleBottomAppBar
import com.example.squiggleheros.composables.SimpleTopAppBar
import androidx.compose.ui.platform.LocalContext

@Composable
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

}
