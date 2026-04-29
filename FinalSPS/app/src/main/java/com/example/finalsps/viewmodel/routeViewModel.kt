package com.example.finalsps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalsps.dataClasses.Place
import com.example.finalsps.screens.getRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class RouteViewModel : ViewModel() {


    // Current route points (polyline)
    private val _routePoints = MutableStateFlow<List<GeoPoint>>(emptyList())
    val routePoints: StateFlow<List<GeoPoint>> = _routePoints

    // Directions list
    private val _directions = MutableStateFlow<List<String>>(emptyList())
    val directions: StateFlow<List<String>> = _directions

    // -----------------------------
    // BUILD ROUTE
    // -----------------------------
    fun buildRoute(start: GeoPoint, end: Place) {

        getRoute(start, GeoPoint(end.latitude, end.longitude)) { points, steps, _, _ ->
            _routePoints.value = points
            _directions.value = steps
        }
    }


    // CANCEL ROUTE

    fun cancelRoute() {
        _routePoints.value = emptyList()
        _directions.value = emptyList()
    }
}

