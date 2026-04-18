package com.example.navigationapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.navigationapp.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val sampleRooms = listOf(
        Room(
            id = "1",
            roomNumber = "A101",
            buildingName = "Science Building",
            floor = "Ground Floor",
            lecturerName = "Dr. Ndlovu",
            courseCode = "CSC101",
            directions = listOf(
                "Enter the Science Building through the main entrance.",
                "Walk straight down the corridor.",
                "Room A101 is on the left."
            )
        ),
        Room(
            id = "2",
            roomNumber = "B204",
            buildingName = "Engineering Block",
            floor = "Second Floor",
            lecturerName = "Mr. Shikongo",
            courseCode = "ENG205",
            directions = listOf(
                "Enter the Engineering Block.",
                "Take the stairs to the second floor.",
                "Turn right.",
                "Room B204 is at the end of the hallway."
            )
        ),
        Room(
            id = "3",
            roomNumber = "C310",
            buildingName = "Main Library Wing",
            floor = "Third Floor",
            lecturerName = "Ms. Amutenya",
            courseCode = "BUS302",
            directions = listOf(
                "Enter the Library Wing.",
                "Use the elevator to the third floor.",
                "Walk toward the reading section.",
                "Room C310 is next to the seminar hall."
            )
        )
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun searchRooms() {
        val query = _uiState.value.query.trim()

        val filtered = if (query.isBlank()) {
            sampleRooms
        } else {
            sampleRooms.filter {
                it.courseCode.contains(query, ignoreCase = true) ||
                        it.lecturerName.contains(query, ignoreCase = true) ||
                        it.roomNumber.contains(query, ignoreCase = true) ||
                        it.buildingName.contains(query, ignoreCase = true)
            }
        }

        _uiState.update { it.copy(results = filtered) }
    }

    fun selectRoom(room: Room) {
        _uiState.update {
            it.copy(
                selectedRoom = room,
                isNavigating = false,
                progress = 0f
            )
        }
    }

    fun startNavigation() {
        _uiState.update {
            it.copy(
                isNavigating = true,
                progress = 0.2f
            )
        }
    }

    fun stopNavigation() {
        _uiState.update {
            it.copy(
                isNavigating = false,
                progress = 0f
            )
        }
    }

    fun simulateProgressStep() {
        val current = _uiState.value
        if (!current.isNavigating) return

        val newProgress = (current.progress + 0.2f).coerceAtMost(1f)

        _uiState.update {
            it.copy(
                progress = newProgress,
                isNavigating = newProgress < 1f
            )
        }
    }

    fun toggleBookmark(room: Room) {
        val currentBookmarks = _uiState.value.bookmarkedRooms.toMutableList()

        val alreadyBookmarked = currentBookmarks.any { it.id == room.id }

        if (alreadyBookmarked) {
            currentBookmarks.removeAll { it.id == room.id }
        } else {
            currentBookmarks.add(room)
        }

        _uiState.update {
            it.copy(bookmarkedRooms = currentBookmarks)
        }
    }

    fun isBookmarked(room: Room): Boolean {
        return _uiState.value.bookmarkedRooms.any { it.id == room.id }
    }
}