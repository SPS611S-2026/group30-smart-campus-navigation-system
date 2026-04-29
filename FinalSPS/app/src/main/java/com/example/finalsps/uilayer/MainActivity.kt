package com.example.finalsps.uilayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.finalsps.dataClasses.Place
import com.example.finalsps.nav.Screen
import com.example.finalsps.screens.OSMMapview
import com.example.finalsps.screens.SearchScreen
import com.example.finalsps.screens.NavigationMapScreen
import com.example.finalsps.screens.BookmarksScreen
import com.example.finalsps.viewmodel.MainViewModel
import com.example.finalsps.ui.theme.FinalSPSTheme
import com.example.finalsps.components.CampusBottomNav
import android.os.Bundle
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { }

    @SuppressLint("ContextCastToActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        setContent {
            FinalSPSTheme {
                val vm: MainViewModel = viewModel()
                val state = vm.uiState.collectAsState().value
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val context = LocalContext.current as MainActivity

                LaunchedEffect(Unit) {
                    vm.loadPlaces(context)
                }

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                // GPS center stub
                                scope.launch { }
                            }
                        ) {
                            Icon(Icons.Default.MyLocation, "My Location")
                        }
                    },
                    bottomBar = { CampusBottomNav(navController = navController) }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Map.route,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable(Screen.Map.route) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                OSMMapview(
                                    onSearch = { navController.navigate(Screen.Search.route) },
                                    onNavigate = { navController.navigate(Screen.Nav.route) }
                                )
                            }
                        }
                        composable(Screen.Search.route) {
                            SearchScreen(
                                uiState = state,
                                onQueryChange = vm::onQueryChange,
                                onSearch = vm::search,
                                onSelect = { place ->
                                    vm.selectPlace(place)
                                    navController.navigate(Screen.Map.route) {
                                        popUpTo(Screen.Map.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Bookmark.route) {
                            BookmarksScreen(
                                uiState = state,
                                onSelect = { place ->
                                    vm.selectPlace(place)
                                    navController.navigate(Screen.Map.route) {
                                        popUpTo(Screen.Map.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Nav.route) {
                            NavigationMapScreen(
                                destination = state.selectedPlace,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
