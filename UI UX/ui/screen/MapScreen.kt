package com.example.navigationapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.navigationapp.model.Room
import com.example.navigationapp.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onStartNavigation: () -> Unit,
    onStopNavigation: () -> Unit,
    onProgressStep: () -> Unit,
    onToggleBookmark: (Room) -> Unit,
    isBookmarked: (Room) -> Boolean
) {
    val room = uiState.selectedRoom

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Room Details") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        color = Color(0xFFE6EEF7),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (room != null) {
                        "Offline Building Map Placeholder\n${room.buildingName} - ${room.roomNumber}"
                    } else {
                        "No room selected"
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (room != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Room ${room.roomNumber}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Building: ${room.buildingName}")
                        Text("Floor: ${room.floor}")
                        Text("Course: ${room.courseCode}")
                        Text("Lecturer: ${room.lecturerName}")

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onToggleBookmark(room) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (isBookmarked(room)) "Remove Bookmark" else "Bookmark Room"
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Directions",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        room.directions.forEachIndexed { index, step ->
                            Text("${index + 1}. $step")
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (uiState.isNavigating) {
                            Text("Navigation in progress")
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { uiState.progress },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = onProgressStep,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Next Step")
                                }

                                Button(
                                    onClick = onStopNavigation,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Stop")
                                }
                            }
                        } else {
                            Button(
                                onClick = onStartNavigation,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Start Navigation")
                            }
                        }
                    }
                }
            }
        }
    }
}