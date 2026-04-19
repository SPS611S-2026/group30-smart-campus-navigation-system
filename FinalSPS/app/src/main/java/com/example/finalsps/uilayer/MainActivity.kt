package com.example.finalsps.uilayer

import com.example.finalsps.viewmodel.MainViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalsps.dataClasses.Place
import com.example.finalsps.screens.MapScreen
import com.example.finalsps.screens.OSMMapview

import com.example.finalsps.screens.SearchScreen

import com.example.navigationapp.ui.screen.NavigationMapScreen
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val vm: MainViewModel = viewModel()
            val state = vm.uiState.collectAsState().value

            Configuration.getInstance().load(
                applicationContext,
                getSharedPreferences("osmdroid", MODE_PRIVATE)
            )

            var screen by remember { mutableStateOf("map") }
            var selectedPlace by remember { mutableStateOf<Place?>(null) }



            //screen switching
            when (screen) {

                "map" -> Column {

                    OSMMapview(
                        onSearch = { screen = "search" },
                        onNavigate = {
                            if (selectedPlace != null) screen = "nav"
                        }
                    )

                    // buttons stay inside map screen ONLY
                }

                "search" -> SearchScreen(
                    uiState = state,
                    onQueryChange = vm::onQueryChange,
                    onSearch = vm::search,
                    onSelect = {
                        selectedPlace = it
                        vm.selectPlace(it)
                        screen = "map"   // go back to map immediately
                    }
                )

                "nav" -> NavigationMapScreen(
                    destination = selectedPlace,
                    onBack = { screen = "map" }
                )
            }
        }
    }
}