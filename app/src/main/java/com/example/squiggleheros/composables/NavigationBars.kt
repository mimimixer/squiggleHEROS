package com.example.squiggleheros.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.squiggleheros.navigation.bottomNavigationIcons


//creates a simple TopAppBar with centered title
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(topBarText: String, backArrow: Boolean, navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BackArrow(hasArrow = backArrow, navController = navController)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = topBarText)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun SimpleBottomAppBar(navController: NavController) {
    BottomNavigation(modifier = Modifier
        //10-3-24 finally found how to round corners
        //https://stackoverflow.com/questions/72270597/bottom-nav-bar-with-curved-edge-and-shadow-jetpack-compose
        .graphicsLayer {
            clip = true
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            shadowElevation = 2.2f
        }
    ) {// 24.03.29 https://developer.android.com/develop/ui/compose/navigation#bottom-nav
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        println(currentDestination.toString())
        var select = false

        bottomNavigationIcons.forEach { item ->
            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any {
                    it.route == item.route
                } == true,

                onClick = {
                    select = !select
                    navController.navigate(route = item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            //navController.previousBackStackEntry?.destination?.id
                            saveState = true
                            //inclusive = true
                        }
                        // Avoid multiple copies of the same destination on top of stack when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        //restoreState = true

                    }
                },
                icon = {
                    Icon(
                        imageVector = if (select) {
                            item.selectedIcon
                        } else {
                            item.unselectedIcon
                        },
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                }
            )
        }
    }
}

@Composable
fun BackArrow(hasArrow: Boolean, navController: NavController){
    if (hasArrow) {
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            modifier = Modifier
                .clickable {
                    navController.popBackStack()
                },
            contentDescription = "navigate Back"
        )
    }
}