package com.example.finalsps.uilayer

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalsps.dataClasses.Place
import com.example.finalsps.viewmodel.MainViewModel
import com.example.finalsps.screens.OSMMapview
import com.example.finalsps.screens.SearchScreen
import com.example.finalsps.screens.NavigationMapScreen

class MainActivity : ComponentActivity() {
    // -----------------------------
// LOCATION PERMISSION HANDLER
// -----------------------------
    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
// Request permissions immediately
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        setContent {

            val vm: MainViewModel = viewModel()
            val state = vm.uiState.collectAsState().value

            var screen by remember { mutableStateOf("map") }
            var selectedPlace by remember { mutableStateOf<Place?>(null) }

            // CORRECT PLACE FOR LOADING DATA
            LaunchedEffect(Unit) {
                vm.loadPlaces(this@MainActivity)
            }

            when (screen) {

                "map" -> OSMMapview(
                    onSearch = { screen = "search" },
                    onNavigate = {
                        if (selectedPlace != null) {
                            screen = "nav"
                        }
                    }
                )

                "search" -> SearchScreen(
                    uiState = state,
                    onQueryChange = vm::onQueryChange,
                    onSearch = vm::search,
                    onSelect = { place ->
                        selectedPlace = place
                        vm.selectPlace(place)
                        screen = "map"
                    }
                )

                "nav" -> NavigationMapScreen(
                    destination = selectedPlace,
                    onBack = {
                        screen = "map"
                    }
                )
            }
        }
    }
}