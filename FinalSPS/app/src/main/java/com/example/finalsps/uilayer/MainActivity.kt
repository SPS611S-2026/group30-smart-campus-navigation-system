package com.example.finalsps.UILayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalsps.dataClasses.Place
import com.example.finalsps.screens.OSMMapview
import com.example.finalsps.ui.screen.SearchScreen
import com.example.finalsps.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val vm: MainViewModel = viewModel()
            val state = vm.uiState.collectAsState().value

            var screen by remember { mutableStateOf("map") }

            var selectedPlace by remember {
                mutableStateOf<Place?>(null)
            }

            when (screen) {

                "map" -> OSMMapview(
                    onSearch = { screen = "search" },
                    onNavigate = { screen = "nav" }
                )

                "search" -> SearchScreen(
                    uiState = state,
                    onQueryChange = vm::onQueryChange,
                    onSearch = vm::search,
                    onSelect = {
                        selectedPlace = it
                        vm.selectPlace(it)
                        screen = "map"
                    }
                )

                "nav" -> {

                }
            }
        }
    }
}