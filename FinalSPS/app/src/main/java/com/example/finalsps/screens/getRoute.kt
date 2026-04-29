package com.example.finalsps.screens

import android.os.Handler
import android.os.Looper
import okhttp3.*
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.io.IOException

fun getRoute(
    start: GeoPoint,
    end: GeoPoint,
    onResult: (List<GeoPoint>, List<String>, Double, Double) -> Unit
) {

    // -----------------------------
    // OSRM ROUTING API URL
    // -----------------------------
    val url =
        "https://router.project-osrm.org/route/v1/walking/" +
                "${start.longitude},${start.latitude};" +
                "${end.longitude},${end.latitude}" +
                "?overview=full&geometries=geojson&steps=true"

    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            // ----------------------------------------
            // Always return safe fallback on UI thread
            // ----------------------------------------
            Handler(Looper.getMainLooper()).post {
                onResult(emptyList(), listOf("Route failed"), 0.0, 0.0)
            }
        }

        override fun onResponse(call: Call, response: Response) {

            val body = response.body?.string() ?: return
            val json = JSONObject(body)

            val routes = json.getJSONArray("routes")

            if (routes.length() == 0) return

            val route = routes.getJSONObject(0)

            val distance = route.getDouble("distance")
            val duration = route.getDouble("duration")

            // -----------------------------
            // TURN BY TURN DIRECTIONS
            // -----------------------------
            val stepsArray = route
                .getJSONArray("legs")
                .getJSONObject(0)
                .getJSONArray("steps")

            val directions = mutableListOf<String>()

            for (i in 0 until stepsArray.length()) {

                val step = stepsArray.getJSONObject(i)

                val name = step.optString("name", "")
                val type = step.getJSONObject("maneuver").optString("type", "")

                val instruction = when (type) {

                    "depart" -> "Start ${if (name.isNotBlank()) "on $name" else ""}"
                    "turn" -> "Turn ${if (name.isNotBlank()) "onto $name" else ""}"
                    "arrive" -> "You have arrived"

                    else -> if (name.isNotBlank()) "$type onto $name" else type
                }

                directions.add(instruction)
            }

            // -----------------------------
            // ROUTE POLYLINE POINTS
            // -----------------------------
            val coords = route
                .getJSONObject("geometry")
                .getJSONArray("coordinates")

            val points = mutableListOf<GeoPoint>()

            for (i in 0 until coords.length()) {

                val c = coords.getJSONArray(i)

                points.add(
                    GeoPoint(
                        c.getDouble(1),
                        c.getDouble(0)
                    )
                )
            }
            // -----------------------------
            // Return results on main thread
            // -----------------------------
            Handler(Looper.getMainLooper()).post {
                onResult(points, directions, distance, duration)
            }
        }
    })
}