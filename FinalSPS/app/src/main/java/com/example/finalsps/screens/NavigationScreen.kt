package com.example.finalsps.screens

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.finalsps.components.getRoute
import com.example.finalsps.dataClasses.Place
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun NavigationMapScreen(
    destination: Place?,     // destination selected from search/map
    onBack: () -> Unit       // return to previous screen
) {

    // -----------------------------
    // STATE: holds route & steps
    // -----------------------------
    var directions by remember { mutableStateOf<List<String>>(emptyList()) }

    var mapRef by remember { mutableStateOf<MapView?>(null) }
    var routeLine by remember { mutableStateOf<Polyline?>(null) }

    Column {

        // =========================
        // TOP BAR (BACK + CANCEL)
        // =========================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // BACK BUTTON → returns to map screen
            Button(onClick = onBack) {
                Text("Back")
            }

            // CANCEL ROUTE BUTTON
            Button(onClick = {

                // Clear UI directions
                directions = emptyList()

                // Remove polyline from map (REAL cancel fix)
                mapRef?.let { map ->
                    routeLine?.let {
                        map.overlays.remove(it)
                        map.invalidate() // refresh map
                    }
                }

                routeLine = null
            }) {
                Text("Cancel Route")
            }
        }

        // =========================
        // DIRECTIONS LIST PANEL
        // =========================
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(8.dp)
        ) {
            items(directions) { step ->
                Text("• $step")
            }
        }

        // =========================
        // MAP VIEW
        // =========================
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->

                // -------------------------
                // OSMDROID CONFIG INIT
                // -------------------------
                Configuration.getInstance().load(
                    context,
                    context.getSharedPreferences("osmdroid", 0)
                )

                // REQUIRED OR MAP MAY FAIL
                Configuration.getInstance().userAgentValue = context.packageName

                val map = MapView(context)
                mapRef = map // store reference for cancel button

                map.setTileSource(TileSourceFactory.MAPNIK)
                map.setMultiTouchControls(true)

                // =========================
                // USER LOCATION OVERLAY
                // =========================
                val locationOverlay = MyLocationNewOverlay(
                    GpsMyLocationProvider(context),
                    map
                )

                locationOverlay.enableMyLocation()
                locationOverlay.enableFollowLocation()
                map.overlays.add(locationOverlay)

                // =========================
                // DESTINATION POINT
                // =========================
                val end = destination?.let {
                    GeoPoint(it.latitude, it.longitude)
                } ?: GeoPoint(-22.56538, 17.07675)

                // Destination marker
                val marker = Marker(map).apply {
                    position = end
                    title = destination?.name ?: "Destination"
                }

                map.overlays.add(marker)

                // =========================
                // WAIT FOR GPS FIX
                // =========================
                locationOverlay.runOnFirstFix {

                    val start = locationOverlay.myLocation
                        ?: GeoPoint(-22.56538, 17.07675) // fallback

                    // =========================
                    // REQUEST ROUTE FROM OSRM
                    // =========================
                    getRoute(start, end) { points, steps, _, _ ->

                        // Update UI directions
                        directions = steps

                        // If route exists → draw it
                        if (points.isNotEmpty()) {

                            // Remove old route first (important fix)
                            routeLine?.let {
                                map.overlays.remove(it)
                            }

                            // Create new route line
                            val polyline = Polyline().apply {
                                setPoints(points)
                                outlinePaint.color = Color.BLUE
                                outlinePaint.strokeWidth = 10f
                            }

                            routeLine = polyline
                            map.overlays.add(polyline)

                            // Refresh map UI
                            map.post {
                                map.invalidate()
                            }
                        }
                    }
                }

                // =========================
                // INITIAL CAMERA POSITION
                // =========================
                map.controller.setZoom(17.0)
                map.controller.setCenter(end)

                map
            }
        )
    }
}