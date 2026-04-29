package com.example.finalsps.screens

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.example.finalsps.dataClasses.Place

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Arrangement
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

//static map screen
@Composable
fun OSMMapview(
    onSearch: () -> Unit,
    onNavigate: () -> Unit
) {

    Column {


        // MAP
        AndroidView(factory = { context ->

            Configuration.getInstance().load(
                context,
                context.getSharedPreferences("osmdroid", 0)
            )

            val mapView = MapView(context)

            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)

            val campus = GeoPoint(-22.56538, 17.07675)

            mapView.controller.setZoom(17.8)
            mapView.controller.setCenter(campus)

            val marker = Marker(mapView).apply {
                position = campus
                title = "NUST Campus"
            }

            mapView.overlays.add(marker)

            mapView
        },
            modifier = Modifier.weight(1f)
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(onClick = onSearch) {
                Text("Search")
            }

            Button(onClick = onNavigate) {
                Text("Navigate")
            }
        }
    }
}

