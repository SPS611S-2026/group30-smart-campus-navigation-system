package com.example.finalsps.screens

import org.osmdroid.util.GeoPoint
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

fun getRoute(
    start: GeoPoint,
    end: GeoPoint,
    onResult: (List<GeoPoint>, Double, Double) -> Unit
) {

    val url =
        "https://router.project-osrm.org/route/v1/walking/" +
                "${start.longitude},${start.latitude};" +
                "${end.longitude},${end.latitude}?overview=full&geometries=geojson"

    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {

            val body = response.body?.string() ?: return
            val json = JSONObject(body)

            val route = json.getJSONArray("routes").getJSONObject(0)

            val distance = route.getDouble("distance")
            val duration = route.getDouble("duration")

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

            onResult(points, distance, duration)
        }
    })
}