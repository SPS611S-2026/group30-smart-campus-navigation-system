package com.example.finalsps.screens

import com.example.finalsps.dataClasses.Place
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.example.finalsps.dataClasses.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: UiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSelect: (Place) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Campus Finder") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search") }
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onSearch,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search")
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn {
                items(uiState.results) { place ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .clickable { onSelect(place) }
                    ) {

                        Column(Modifier.padding(12.dp)) {
                            Text(place.name, fontWeight = FontWeight.Bold)
                            Text("Room: ${place.roomNumber}")
                            Text("Building: ${place.building}")
                            Text("Floor: ${place.floorNumber}")
                            Text("Description: ${place.description}"
                            )

                        }
                    }
                }
            }
        }
    }
}