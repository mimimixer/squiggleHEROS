package com.example.squiggleheros.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.squiggleheros.Greeting
import com.example.squiggleheros.R
import com.example.squiggleheros.composables.SimpleBottomAppBar
import com.example.squiggleheros.composables.SimpleTopAppBar

@Composable
fun DetailScreen (squiggleID: String?, navController: NavController){

    Scaffold(
        topBar = {
            SimpleTopAppBar(getString(LocalContext.current, R.string.detail_screen), true, navController)
        },
        bottomBar = {
            SimpleBottomAppBar(navController)
        }
    ) { values ->
        Greeting(
            name = getString(LocalContext.current, R.string.detail_screen),
            paddingValues = values
        )
    }

}