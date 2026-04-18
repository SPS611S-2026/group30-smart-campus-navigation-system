package com.example.navigationapp.viewmodel

import com.example.navigationapp.model.Room

data class UiState(
    val query: String = "",
    val results: List<Room> = emptyList(),
    val selectedRoom: Room? = null,
    val isNavigating: Boolean = false,
    val progress: Float = 0f,
    val bookmarkedRooms: List<Room> = emptyList(),
    val showOfflineMap: Boolean = true
)