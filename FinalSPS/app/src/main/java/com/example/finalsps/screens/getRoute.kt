package com.example.finalsps.screens

import org.osmdroid.util.GeoPoint
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.os.Handler
import android.os.Looper

fun getRoute(
    start: GeoPoint,
    end: GeoPoint,
    onResult: (List<GeoPoint>, List<String>, Double, Double) -> Unit
) {

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
            e.printStackTrace()

            // Return safe fallback on main thread
            Handler(Looper.getMainLooper()).post {
                onResult(
                    emptyList(),
                    listOf("Failed to load route"),
                    0.0,
                    0.0
                )
            }
        }

        override fun onResponse(call: Call, response: Response) {

            val body = response.body?.string() ?: return

            val json = JSONObject(body)

            val routesArray = json.getJSONArray("routes")
            if (routesArray.length() == 0) return

            val route = routesArray.getJSONObject(0)

            val distance = route.getDouble("distance")
            val duration = route.getDouble("duration")

            // =========================
            // 🧭 DIRECTIONS (STEPS)
            // =========================
            val stepsArray = route
                .getJSONArray("legs")
                .getJSONObject(0)
                .getJSONArray("steps")

            val directions = mutableListOf<String>()

            for (i in 0 until stepsArray.length()) {

                val step = stepsArray.getJSONObject(i)

                val name = step.optString("name", "")
                val maneuver = step
                    .getJSONObject("maneuver")
                    .optString("type", "")

                val instruction = when (maneuver) {
                    "turn" -> if (name.isNotBlank()) "Turn onto $name" else "Turn"
                    "depart" -> if (name.isNotBlank()) "Start on $name" else "Start"
                    "arrive" -> "You have arrived"
                    "merge" -> if (name.isNotBlank()) "Merge onto $name" else "Merge"
                    "roundabout" -> "Enter roundabout"
                    else -> if (name.isNotBlank()) "$maneuver on $name" else maneuver
                }

                directions.add(instruction)
            }

            // =========================
            // 🗺️ POLYLINE (MAP ROUTE)
            // =========================
            val coords = route
                .getJSONObject("geometry")
                .getJSONArray("coordinates")

            val points = mutableListOf<GeoPoint>()

            for (i in 0 until coords.length()) {

                val c = coords.getJSONArray(i)

                points.add(
                    GeoPoint(
                        c.getDouble(1), // lat
                        c.getDouble(0)  // lon
                    )
                )
            }

            // =========================
            // 🔥 RETURN ON MAIN THREAD
            // =========================
            Handler(Looper.getMainLooper()).post {
                onResult(points, directions, distance, duration)
            }
        }
    })
}