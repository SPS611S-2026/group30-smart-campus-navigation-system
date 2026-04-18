package com.nust.smartnavigate;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.nust.smartnavigate.models.Place;
import com.nust.smartnavigate.repository.PlaceRepository;

import java.util.List;

/**
 * MainActivity is the entry point of the Smart Navigate app.
 * It demonstrates loading places from JSON and searching them.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create repository and load places.json
        PlaceRepository repo = new PlaceRepository(this);

        // Print all places
        List<Place> allPlaces = repo.getAllPlaces();
        for (Place p : allPlaces) {
            Log.d("AllPlaces", p.toString());
        }

        // Search example
        List<Place> results = repo.search("Fabianus");
        for (Place p : results) {
            Log.d("SearchResult", "Found: " + p.getName() + " - " + p.getRoomNumber());
        }
    }
}
