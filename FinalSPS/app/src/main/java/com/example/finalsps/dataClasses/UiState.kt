package com.example.finalsps.dataClasses

data class UiState(
    val query: String = "",
    val results: List<Place> = emptyList(),
    val selectedPlace: Place? = null,
    val bookmarked: List<Place> = emptyList(),
    val isNavigating: Boolean = false
)