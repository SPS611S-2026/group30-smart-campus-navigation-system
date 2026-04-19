package com.example.finalsps.dataClasses

data class UiState(
    val query: String = "",
    val results: List<Place> = emptyList(),
    val selectedPlace: Place? = null,
    val isNavigating: Boolean = false,
    val progress: Float = 0f,
    val bookmarked: List<Place> = emptyList()
)