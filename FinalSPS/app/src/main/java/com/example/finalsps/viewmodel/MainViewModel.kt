package com.example.finalsps.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.finalsps.dataClasses.Place
import com.example.finalsps.dataClasses.PlaceRepository
import com.example.finalsps.dataClasses.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private var allPlaces: List<Place> = emptyList()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun loadPlaces(context: Context) {
        allPlaces = PlaceRepository.loadPlaces(context)
        _uiState.update { it.copy(results = allPlaces) }
    }

    fun onQueryChange(q: String) {
        _uiState.update { it.copy(query = q) }
    }

    fun search() {
        val q = uiState.value.query

        val filtered = if (q.isBlank()) {
            allPlaces
        } else {
            allPlaces.filter {
                it.name.contains(q, true) ||
                        it.roomNumber.contains(q, true) ||
                        it.courseCode.contains(q, true) ||
                        it.lecturer.contains(q, true)
            }
        }

        _uiState.update { it.copy(results = filtered) }
    }

    fun selectPlace(place: Place) {
        _uiState.update { it.copy(selectedPlace = place) }
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