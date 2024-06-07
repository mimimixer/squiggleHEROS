package com.example.squiggleheros.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.squiggleheros.Greeting
import com.example.squiggleheros.composables.SimpleBottomAppBar
import com.example.squiggleheros.composables.SimpleTopAppBar

@Composable
fun CanvasScreen (navController: NavController){

    Scaffold(
        topBar = {
            SimpleTopAppBar("SquiggleHERO", false, navController)
        },
        bottomBar = {
            SimpleBottomAppBar(navController)
        }
    ) { values ->
        Greeting(
            name = "Canvasscreen",
            paddingValues = values
        )
    }

}
