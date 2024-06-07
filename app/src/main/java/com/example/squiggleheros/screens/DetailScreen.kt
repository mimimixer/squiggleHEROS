package com.example.squiggleheros.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.squiggleheros.Greeting
import com.example.squiggleheros.composables.SimpleBottomAppBar
import com.example.squiggleheros.composables.SimpleTopAppBar

@Composable
fun DetailScreen (squiggleID: String?, navController: NavController){

    Scaffold(
        topBar = {
            SimpleTopAppBar("squiggleHERO Detail", true, navController)
        },
        bottomBar = {
            SimpleBottomAppBar(navController)
        }
    ) { values ->
        Greeting(
            name = "Detailscreen",
            paddingValues = values
        )
    }

}