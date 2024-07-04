package com.example.squiggleheros

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.squiggleheros.navigation.Navigation
import com.example.squiggleheros.ui.theme.SquiggleHEROSTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SquiggleHEROSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    //deleteDatabase(MovieDatabase.movie_db)
                    Navigation()
                }
            }
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}

@Composable
fun Greeting(name: String, paddingValues: PaddingValues) {
    Text(
        text = "Hello $name!",
        modifier = Modifier.padding(paddingValues)
    )
}

//@Preview(showBackground = true)

val paddingVals = PaddingValues(16.dp)
@Composable
fun GreetingPreview() {
    SquiggleHEROSTheme {
        Greeting("Android", paddingVals )
    }
}