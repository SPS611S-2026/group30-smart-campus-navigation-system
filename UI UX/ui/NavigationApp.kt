package com.example.navigationapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigationapp.ui.screen.MapScreen
import com.example.navigationapp.ui.screen.SearchScreen
import com.example.navigationapp.viewmodel.MainViewModel

@Composable
fun NavigationApp(
    viewModel: MainViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        composable("search") {
            SearchScreen(
                uiState = uiState,
                onQueryChange = viewModel::onQueryChange,
                onSearch = viewModel::searchRooms,
                onRoomSelected = {
                    viewModel.selectRoom(it)
                    navController.navigate("map")
                }
            )
        }

        composable("map") {
            MapScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onStartNavigation = viewModel::startNavigation,
                onStopNavigation = viewModel::stopNavigation,
                onProgressStep = viewModel::simulateProgressStep,
                onToggleBookmark = viewModel::toggleBookmark,
                isBookmarked = viewModel::isBookmarked
            )
        }
    }
}