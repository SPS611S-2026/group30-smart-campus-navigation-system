package mapClasses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import mapClasses.ui.MapScreen
import mapClasses.ui.NavigationMap

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var screen by remember { mutableStateOf("nav") }

            when (screen) {
                "map" -> MapScreen()
                "nav" -> NavigationMap()
            }
        }
    }
}