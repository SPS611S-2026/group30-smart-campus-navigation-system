package mapClasses

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
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
fun MapScreen() {

    AndroidView(factory = { context ->

        // Load config
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", 0)
        )

        val mapView = MapView(context)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // center on nust office building for now
        val nust = GeoPoint(-22.565387729593812, 17.076756945767954)

        mapView.controller.setZoom(17.8)
        mapView.controller.setCenter(nust)

        // zoom limit
        mapView.minZoomLevel = 17.8
        mapView.maxZoomLevel = 20.8

        // marker for position display
        val marker = Marker(mapView)
        marker.position = nust
        marker.title = "NUST Office (Storch Street)"
        mapView.overlays.add(marker)

        // bounds of NUST campus map
        val points = listOf(
            GeoPoint(-22.563994, 17.075275),
            GeoPoint(-22.567880, 17.069892),
            GeoPoint(-22.567645, 17.077762),
            GeoPoint(-22.566649, 17.078869)
        )

        val north = points.maxOf { it.latitude }
        val south = points.minOf { it.latitude }
        val east = points.maxOf { it.longitude }
        val west = points.minOf { it.longitude }

        val boundingBox = BoundingBox(
            north + 0.005,
            east + 0.005,
            south - 0.005,
            west - 0.005
        )

        mapView.setScrollableAreaLimitDouble(boundingBox)

        mapView
    })
}

//navigation for live map
@Composable
fun NavigationMap() {

    AndroidView(factory = { context ->

        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", 0)
        )

        val mapView = MapView(context)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(17.8)


        // zoom limit
        mapView.minZoomLevel = 17.8
        mapView.maxZoomLevel = 20.8

        // lock map into area of nust
        val points = listOf(
            GeoPoint(-22.563994, 17.075275),
            GeoPoint(-22.567880, 17.069892),
            GeoPoint(-22.567645, 17.077762),
            GeoPoint(-22.566649, 17.078869)
        )

        val north = points.maxOf { it.latitude }
        val south = points.minOf { it.latitude }
        val east = points.maxOf { it.longitude }
        val west = points.minOf { it.longitude }

        val boundingBox = BoundingBox(
            north + 0.005,
            east + 0.005,
            south - 0.005,
            west - 0.005
        )

        mapView.setScrollableAreaLimitDouble(boundingBox)

        mapView

        // user location
        val locationOverlay = MyLocationNewOverlay(mapView).apply {
            enableMyLocation()
            enableFollowLocation()
        }
        mapView.overlays.add(locationOverlay)

        //state of destination
        var destination = GeoPoint(-22.5670, 17.0740)

        val destinationMarker = Marker(mapView).apply {
            position = destination
            title = "Destination"
        }
        mapView.overlays.add(destinationMarker)

        val routeLine = Polyline().apply {
            outlinePaint.strokeWidth = 8f
            outlinePaint.color = android.graphics.Color.BLUE
        }
        mapView.overlays.add(routeLine)

        val nustCenter = GeoPoint(-22.5653877, 17.0767569)
        mapView.controller.setCenter(nustCenter)

        //routing
        fun startNavigation(current: GeoPoint) {

            getRoute(current, destination) { points, _, _ ->

                mapView.post {
                    routeLine.setPoints(points)
                    mapView.invalidate()
                }
            }
        }

        // auto update path loop
        var lastLocation: GeoPoint? = null
        var navigating = true

        mapView.postDelayed(object : Runnable {
            override fun run() {

                if (navigating) {

                    val currentLoc = locationOverlay.myLocation

                    if (currentLoc != null) {

                        val current = GeoPoint(
                            currentLoc.latitude,
                            currentLoc.longitude
                        )

                        if (lastLocation == null ||
                            current.distanceToAsDouble(lastLocation) > 10
                        ) {
                            startNavigation(current)
                            lastLocation = current
                        }

                        mapView.controller.animateTo(current)
                    }
                }

                mapView.postDelayed(this, 5000)
            }
        }, 2000)

        mapView
    })
}
//osrm routing service
fun getRoute(
    start: GeoPoint,
    end: GeoPoint,
    onResult: (List<GeoPoint>, Double, Double) -> Unit
) {

    val url =
        "https://router.project-osrm.org/route/v1/walking/" +
                "${start.longitude},${start.latitude};" +
                "${end.longitude},${end.latitude}?overview=full&geometries=geojson"

    val client = okhttp3.OkHttpClient()

    val request = okhttp3.Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : okhttp3.Callback {

        override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {

            val body = response.body?.string() ?: return
            val json = org.json.JSONObject(body)

            val route = json.getJSONArray("routes").getJSONObject(0)

            val distance = route.getDouble("distance")
            val duration = route.getDouble("duration")

            val coords = route
                .getJSONObject("geometry")
                .getJSONArray("coordinates")

            val points = mutableListOf<GeoPoint>()

            for (i in 0 until coords.length()) {
                val c = coords.getJSONArray(i)
                points.add(GeoPoint(c.getDouble(1), c.getDouble(0)))
            }

            onResult(points, distance, duration)
        }
    })
}