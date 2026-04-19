package com.example.navigationapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.finalsps.dataClasses.Place
import com.example.finalsps.dataClasses.UiState
import com.example.finalsps.screens.getRoute
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun NavigationMapScreen(
    destination: Place?,
    onBack: () -> Unit
) {

    AndroidView(factory = { context ->

        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", 0)
        )

        val map = MapView(context)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // START + END
        val start = GeoPoint(-22.56538, 17.07675)

        val end = destination?.let {
            GeoPoint(it.latitude, it.longitude)
        } ?: GeoPoint(-22.56538, 17.07675)

        // MARKER
        val marker = Marker(map).apply {
            position = end
            title = destination?.name ?: "Destination"
        }

        map.overlays.add(marker)

        // ROUTE
        getRoute(start, end) { points, _, _ ->

            if (points.isEmpty()) return@getRoute

            val road = Polyline().apply {
                setPoints(points)
                outlinePaint.color = android.graphics.Color.BLUE
                outlinePaint.strokeWidth = 8f
            }

            map.overlays.add(road)
            map.invalidate()
        }

        map.controller.setZoom(17.5)
        map.controller.setCenter(end)

        map
    })
}