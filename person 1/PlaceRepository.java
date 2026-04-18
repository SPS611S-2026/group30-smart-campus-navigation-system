package com.nust.smartnavigate.repository;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nust.smartnavigate.models.Place;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class to load and search Places from JSON dataset.
 */
public class PlaceRepository {
    private List<Place> places;

    // Constructor: loads places.json from assets
    public PlaceRepository(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("places.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            places = new Gson().fromJson(reader, new TypeToken<List<Place>>(){}.getType());
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            places = new ArrayList<>(); // fallback empty list
        }
    }

    // Return all places
    public List<Place> getAllPlaces() {
        return places;
    }

    // Search places by name, room, or description
    public List<Place> search(String query) {
        List<Place> results = new ArrayList<>();
        for (Place p : places) {
            if (p.getName().toLowerCase().contains(query.toLowerCase()) ||
                p.getRoomNumber().toLowerCase().contains(query.toLowerCase()) ||
                p.getDescription().toLowerCase().contains(query.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }
}
