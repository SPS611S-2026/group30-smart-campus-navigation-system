package com.example.finalsps.classes

import android.content.Context
import com.example.finalsps.dataClasses.Place
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

object PlaceRepository {

    fun loadPlaces(context: Context): List<Place> {

        val input = context.assets.open("places.json")
        val reader = InputStreamReader(input)

        return Gson().fromJson(
            reader,
            object : TypeToken<List<Place>>() {}.type
        )
    }
}