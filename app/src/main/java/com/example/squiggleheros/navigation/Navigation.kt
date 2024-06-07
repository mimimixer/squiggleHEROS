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


@Composable
    fun Navigation(){
        val navController =  rememberNavController()
        //var moviesViewModel : MoviesViewModel = viewModel()
        NavHost(
            navController = navController,
            startDestination = Screen.Canvas.route
        ){
            composable(route = Screen.Canvas.route)
            {
                CanvasScreen(navController = navController)//, moviesViewModel = moviesViewModel)
            }
            composable(
                route = Screen.Detail.route + "/{$DETAIL_SCREEN_KEY}",
                arguments = listOf(
                    navArgument(name = DETAIL_SCREEN_KEY) {
                        type = NavType.StringType
                    })
            ) {backStackEntry ->
                // val movietitle = getMovies()
                //  .find{ it.id.equals(backStackEntry.arguments?.getString(DETAIL_SCREEN_KEY))}?.title
                // Log.d("Args", "$movietitle")
                println("detailscreenkey, also movieID is ${backStackEntry.arguments?.getString(DETAIL_SCREEN_KEY)}")
                DetailScreen(squiggleID = backStackEntry.arguments?.getString(DETAIL_SCREEN_KEY),
                    navController = navController
                )
                // moviesViewModel = moviesViewModel)
            }
            composable(route = Screen.Gallery.route
            )
            {
                GalleryScreen(navController = navController)//, moviesViewModel)
            }
        }

}