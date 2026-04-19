package com.example.finalsps.viewmodel

import androidx.lifecycle.ViewModel
import com.example.finalsps.dataClasses.Place
import com.example.finalsps.dataClasses.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val samplePlaces = listOf(
        Place(
            id = "1",
            name = "Science Building A101",
            roomNumber = "A101",
            floor = "Ground Floor",
            building = "Science Building",
            lecturer = "Dr. Ndlovu",
            courseCode = "CSC101",
            latitude = -22.56538,
            longitude = 17.07675,
            directions = listOf("Enter building", "Go straight", "Left side")
        ),
        Place(
            id = "2",
            name = "Engineering B204",
            roomNumber = "B204",
            floor = "2nd Floor",
            building = "Engineering Block",
            lecturer = "Mr. Shikongo",
            courseCode = "ENG205",
            latitude = -22.56788,
            longitude = 17.06989,
            directions = listOf("Enter block", "Stairs up", "Right turn")
        )
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onQueryChange(q: String) {
        _uiState.update { it.copy(query = q) }
    }

    fun search() {
        val q = _uiState.value.query

        val filtered = if (q.isBlank()) {
            samplePlaces
        } else {
            samplePlaces.filter {
                it.name.contains(q, true) ||
                        it.roomNumber.contains(q, true) ||
                        it.courseCode.contains(q, true) ||
                        it.lecturer.contains(q, true)
            }
        }

        _uiState.update { it.copy(results = filtered) }
    }

    fun selectPlace(place: Place) {
        _uiState.update {
            it.copy(selectedPlace = place)
        }
    }

    fun toggleBookmark(place: Place) {
        val list = _uiState.value.bookmarked.toMutableList()

        if (list.any { it.id == place.id }) {
            list.removeAll { it.id == place.id }
        } else {
            list.add(place)
        }

        _uiState.update { it.copy(bookmarked = list) }
    }
}