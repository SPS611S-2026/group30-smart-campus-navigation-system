package com.example.finalsps.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.finalsps.dataClasses.Place
import com.example.finalsps.dataClasses.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onStartNavigation: () -> Unit
) {

    val place = uiState.selectedPlace
    Button(onClick = onBack) {
        Text("Back")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Place Details") },
                navigationIcon = {
                    TextButton(onClick = {
                        onStartNavigation()
                    }) {
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
                    text = place?.let {
                        "${it.building} - ${it.roomNumber}"
                    } ?: "No place selected"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (place != null) {

                Card(modifier = Modifier.fillMaxWidth()) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("Building: ${place.building}")
                        Text("Room: ${place.roomNumber}")
                        Text("Floor: ${place.floor}")
                        Text("Course: ${place.courseCode}")
                        Text("Lecturer: ${place.lecturer}")

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Directions",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        place.directions.forEachIndexed { index, step ->
                            Text("${index + 1}. $step")
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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