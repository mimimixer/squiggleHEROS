package com.example.squiggleheros.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.List
import androidx.compose.ui.graphics.vector.ImageVector

const val DETAIL_SCREEN_KEY = "imagePath"

sealed class Screen (
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    object Canvas : Screen(
        title = "Canvas",
        selectedIcon = Icons.Filled.Create,
        unselectedIcon = Icons.Outlined.Create,
        route = "canvas_screen?imagePath={imagePath}"
    )
    object Detail: Screen(
        title = "Detail Screen",
        selectedIcon = Icons.Default.AccountBox,
        unselectedIcon = Icons.Default.AccountBox,
        route = "detail_screen/{$DETAIL_SCREEN_KEY}"
    )
    object Gallery: Screen(
        title = "Gallery",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List,
        route = "gallery_screen"
    )

}


val bottomNavigationIcons = listOf(
    Screen.Canvas,
    Screen.Gallery
)