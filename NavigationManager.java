package com.example.myspps.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.myspps.models.Place;
import com.example.myspps.ui.MapActivity;
import com.example.myspps.ui.ResultsActivity;
import com.example.myspps.ui.SearchActivity;

public class NavigationManager {

    private static final String TAG = "NavigationManager";


    public static final String EXTRA_PLACE_ID    = "extra_place_id";
    public static final String EXTRA_PLACE_NAME  = "extra_place_name";
    public static final String EXTRA_PLACE_ROOM  = "extra_place_room";
    public static final String EXTRA_PLACE_FLOOR = "extra_place_floor";
    public static final String EXTRA_PLACE_LAT   = "extra_place_lat";
    public static final String EXTRA_PLACE_LNG   = "extra_place_lng";
    public static final String EXTRA_PLACE_DESC  = "extra_place_desc";
    public static final String EXTRA_SEARCH_QUERY = "extra_search_query";
    public static void goToSearch(Context context) {
        Log.d(TAG, "Navigating → SearchActivity");
        context.startActivity(new Intent(context, SearchActivity.class));
    }


    public static void goToResults(Context context, String searchQuery) {
        Log.d(TAG, "Navigating → ResultsActivity  query=\"" + searchQuery + "\"");
        Intent intent = new Intent(context, ResultsActivity.class);
        intent.putExtra(EXTRA_SEARCH_QUERY, searchQuery);
        context.startActivity(intent);
    }

    public static void goToMap(Context context, Place place) {
        if (place == null) {
            Log.e(TAG, "goToMap called with null Place — aborted.");
            return;
        }
        Log.d(TAG, "Navigating → MapActivity  place=\"" + place.getName() + "\"");
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRA_PLACE_ID,    place.getId());
        intent.putExtra(EXTRA_PLACE_NAME,  place.getName());
        intent.putExtra(EXTRA_PLACE_ROOM,  place.getRoomNumber());
        intent.putExtra(EXTRA_PLACE_FLOOR, place.getFloorNumber());
        intent.putExtra(EXTRA_PLACE_LAT,   place.getLatitude());
        intent.putExtra(EXTRA_PLACE_LNG,   place.getLongitude());
        intent.putExtra(EXTRA_PLACE_DESC,  place.getDescription());
        context.startActivity(intent);
    }

    /**
     * extractPlaceFromIntent()
     * Called inside MapActivity.onCreate() to unpack the Place
     * that was packed by goToMap() above.
     *
     * @param intent The Intent received by MapActivity
     * @return Reconstructed Place object, or null if data missing
     */
    public static Place extractPlaceFromIntent(Intent intent) {
        if (intent == null || !intent.hasExtra(EXTRA_PLACE_NAME)) {
            Log.w(TAG, "extractPlaceFromIntent — missing data in Intent.");
            return null;
        }
        Place place = new Place(
                intent.getIntExtra(EXTRA_PLACE_ID, -1),
                intent.getStringExtra(EXTRA_PLACE_NAME),
                intent.getDoubleExtra(EXTRA_PLACE_LAT, 0.0),
                intent.getDoubleExtra(EXTRA_PLACE_LNG, 0.0),
                intent.getStringExtra(EXTRA_PLACE_DESC),
                intent.getStringExtra(EXTRA_PLACE_ROOM),
                intent.getStringExtra(EXTRA_PLACE_FLOOR)
        );
        Log.d(TAG, "Extracted Place from Intent: " + place.getName());
        return place;
    }
}
