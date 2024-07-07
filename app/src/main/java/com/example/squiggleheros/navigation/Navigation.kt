package com.example.squiggleheros.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.squiggleheros.screens.CanvasScreen
import com.example.squiggleheros.screens.DetailScreen
import com.example.squiggleheros.screens.GalleryScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Composable
    fun Navigation(){
        val navController =  rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Screen.Canvas.route
        ){
            composable(
                route = "canvas_screen?imagePath={imagePath}",
                arguments = listOf(navArgument("imagePath") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val imagePath = backStackEntry.arguments?.getString("imagePath")
                println(imagePath)
                CanvasScreen(navController = navController, imagePath = imagePath)
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument(DETAIL_SCREEN_KEY) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val imagePath = backStackEntry.arguments?.getString(DETAIL_SCREEN_KEY)?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                imagePath?.let {
                    println(imagePath)
                    DetailScreen(navController = navController, imagePath = it)
                }
            }

            composable(route = Screen.Gallery.route
            )
            {
                GalleryScreen(navController = navController)//, moviesViewModel)
            }
        }

}