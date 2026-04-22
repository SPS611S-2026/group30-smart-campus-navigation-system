package com.example.navigationapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import com.example.finalsps.dataClasses.Place
import com.example.finalsps.screens.getRoute

import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider

@Composable
fun NavigationMapScreen(
    destination: Place?,
    onBack: () -> Unit
) {

    var directions by remember { mutableStateOf<List<String>>(emptyList()) }

    Column {

        // BACK BUTTON
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Button(onClick = onBack) {
                Text("Back")
            }
        }

        // DIRECTIONS PANEL
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            items(directions) { step ->
                Text(
                    text = "• $step",
                    modifier = Modifier.padding(6.dp)
                )
            }
        }

        //  MAP
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->

                Configuration.getInstance().load(
                    context,
                    context.getSharedPreferences("osmdroid", 0)
                )

                val map = MapView(context)
                map.setTileSource(TileSourceFactory.MAPNIK)
                map.setMultiTouchControls(true)

                // 🔵 LIVE LOCATION
                val locationOverlay = MyLocationNewOverlay(
                    GpsMyLocationProvider(context),
                    map
                )

                locationOverlay.enableMyLocation()
                locationOverlay.enableFollowLocation()
                map.overlays.add(locationOverlay)

                // 📍 DESTINATION
                val end = destination?.let {
                    GeoPoint(it.latitude, it.longitude)
                } ?: GeoPoint(-22.56538, 17.07675)

                // 📌 DESTINATION MARKER
                val marker = Marker(map).apply {
                    position = end
                    title = destination?.name ?: "Destination"
                }

                map.overlays.add(marker)

                //  WAIT FOR GPS THEN ROUTE
                locationOverlay.runOnFirstFix {

                    val userLocation = locationOverlay.myLocation
                        ?: GeoPoint(-22.56538, 17.07675)

                    getRoute(userLocation, end) { points, steps, _, _ ->

                        directions = steps

                        if (points.isEmpty()) return@getRoute

                        val road = Polyline().apply {
                            setPoints(points)
                            outlinePaint.color = android.graphics.Color.BLUE
                            outlinePaint.strokeWidth = 8f
                        }

                        map.overlays.add(road)
                        map.invalidate()
                    }
                }

                map.controller.setZoom(17.5)
                map.controller.setCenter(end)

                map
            }
        )
    }
}