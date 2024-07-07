package com.example.squiggleheros.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.squiggleheros.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBarCanvas(title: String, showGalleryIcon: Boolean, navController: NavController, onSaveClick: () -> Unit,
                          onNewDrawingClick: ()-> Unit, onGalleryClick: ()->Unit) {
    TopAppBar(

        title = { Text(text = title) },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = R.color.Azure),
            titleContentColor = colorResource(id = R.color.Cornflower_Blue)),

        actions = {
            IconButton(onClick = onNewDrawingClick) {
                Icon(
                    painterResource(id = R.drawable.renew),
                    contentDescription = "New Drawing",
                    Modifier.size(30.dp),
                    tint = colorResource(id = R.color.CornflowerBlue)

                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Add spacing between icons
            IconButton(onClick = onSaveClick) {
                Icon(
                    painterResource(id = R.drawable.ic_save),
                    contentDescription = "Save",
                    Modifier.size(30.dp),
                    tint = colorResource(id = R.color.HotPink)
                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Add spacing between icons
                IconButton(onClick = onGalleryClick){//{ navController.navigate(Screen.Gallery.route) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.gallery),
                        contentDescription = "Gallery",
                        Modifier.size(30.dp),
                        tint = colorResource(id = R.color.MediumSeaGreen)
                    )
                }

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAppTopBarDetail(title: String, showGalleryIcon: Boolean, navController: NavController,
                          onNewDrawingClick: ()-> Unit, onGalleryClick: ()->Unit){
    TopAppBar(
        title = { Text(text = title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = R.color.Azure),
            titleContentColor = colorResource(id = R.color.Cornflower_Blue)),
        actions = {
            IconButton(onClick = onNewDrawingClick) {
                Icon(
                    painterResource(id = R.drawable.renew),
                    contentDescription = "New Drawing",
                    Modifier.size(30.dp),
                    tint = colorResource(id = R.color.CornflowerBlue)

                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Add spacing between icons
            IconButton(onClick = onGalleryClick) {//{ navController.navigate(Screen.Gallery.route) }) {
                Icon(
                    painter = painterResource(id = R.drawable.gallery),
                    contentDescription = "Gallery",
                    Modifier.size(30.dp),
                    tint = colorResource(id = R.color.MediumSeaGreen)
                )
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAppTopBarGallery(title: String, showGalleryIcon: Boolean, navController: NavController,
                          onNewDrawingClick: ()-> Unit, onFilterFavoritesClick: ()->Unit , showFavoritesOnly: Boolean){
    TopAppBar(
        title = { Text(text = title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = R.color.Azure),
            titleContentColor = colorResource(id = R.color.Cornflower_Blue)),
        actions = {
            IconButton(onClick = onNewDrawingClick) {
                Icon(
                    painterResource(id = R.drawable.renew),
                    contentDescription = "New Drawing",
                    Modifier.size(30.dp),
                    tint = colorResource(id = R.color.CornflowerBlue)

                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Add spacing between icons
            IconButton(onClick = onFilterFavoritesClick) {
                Icon(
                    painter = painterResource(id = if (showFavoritesOnly) R.drawable.ic_favorite else R.drawable.ic_favorite_border),
                    contentDescription = "Filter Favorites",
                    Modifier.size(30.dp)
                )
            }
        }
    )

}