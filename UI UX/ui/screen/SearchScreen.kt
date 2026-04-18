package com.example.navigationapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.navigationapp.model.Room
import com.example.navigationapp.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: UiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRoomSelected: (Room) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Campus Room Finder") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                label = { Text("Search by course code or lecturer") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSearch,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.results.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "No rooms found yet. Search by course code, lecturer, or room.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(uiState.results) { room ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRoomSelected(room) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Room ${room.roomNumber}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Building: ${room.buildingName}")
                                Text(text = "Floor: ${room.floor}")
                                Text(text = "Course: ${room.courseCode}")
                                Text(text = "Lecturer: ${room.lecturerName}")
                            }
                        }
                    }
                }
            }
        }
    }
}