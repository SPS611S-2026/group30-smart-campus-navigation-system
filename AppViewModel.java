package com.example.myspps.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myspps.models.Place;
import com.example.myspps.repository.PlaceRepository;

import java.util.List;

public class AppViewModel extends AndroidViewModel {

    private static final String TAG = "AppViewModel";


    private final PlaceRepository placeRepository;
    private final MutableLiveData<List<Place>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Place> selectedPlace = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isNavigating = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // ── Constructor ───────────────────────────────────────────

    /**
     * @param application Required by AndroidViewModel so we can
     *                    pass a Context to PlaceRepository without
     *                    leaking an Activity reference.
     */
    public AppViewModel(@NonNull Application application) {
        super(application);
        placeRepository = new PlaceRepository(application);
        Log.d(TAG, "AppViewModel created — PlaceRepository ready with "
                + placeRepository.getAllPlaces().size() + " places.");
    }

    // ============================================================
    // PUBLIC ACTIONS — called by UI activities
    // ============================================================

    /**
     * performSearch()
     *
     * Validates the query, delegates to PlaceRepository.search(),
     * then posts results (or an error) to LiveData so every
     * observing UI component refreshes automatically.
     *
     * @param query Text the user typed in the search bar
     */
    public void performSearch(String query) {

        // Guard: reject blank input
        if (query == null || query.trim().isEmpty()) {
            errorMessage.setValue("Please enter a search term.");
            Log.w(TAG, "Search ignored — empty query.");
            return;
        }

        Log.d(TAG, "Searching for: \"" + query + "\"");
        isLoading.setValue(true);

        try {
            // Delegate to Person 1's search logic
            List<Place> results = placeRepository.search(query.trim());
            searchResults.setValue(results);

            if (results.isEmpty()) {
                errorMessage.setValue("No locations found for \"" + query + "\"");
                Log.d(TAG, "No results for: " + query);
            } else {
                Log.d(TAG, results.size() + " result(s) found for: " + query);
            }

        } catch (Exception e) {
            // Catch unexpected errors so the app never crashes here
            errorMessage.setValue("Search failed. Please try again.");
            Log.e(TAG, "Unexpected search error", e);

        } finally {
            isLoading.setValue(false); // Always hide spinner
        }
    }

    /**
     * selectPlace()
     *
     * Called when a user taps a result card. Stores the chosen
     * Place so MapActivity and the detail panel can display it.
     *
     * @param place The Place the user tapped
     */
    public void selectPlace(Place place) {
        if (place == null) { return; }
        Log.d(TAG, "Place selected: " + place.getName());
        selectedPlace.setValue(place);
    }

    /**
     * startNavigation()
     *
     * Validates a destination is chosen, then sets isNavigating
     * to TRUE. Person 3's map module observes this and begins
     * drawing the route + giving turn-by-turn updates.
     */
    public void startNavigation() {
        if (selectedPlace.getValue() == null) {
            errorMessage.setValue("Please select a destination first.");
            return;
        }
        Log.d(TAG, "Navigation STARTED → " + selectedPlace.getValue().getName());
        isNavigating.setValue(true);
    }

    /**
     * stopNavigation()
     *
     * Sets isNavigating to FALSE. Person 3's map module stops
     * the route and GPS tracking updates.
     */
    public void stopNavigation() {
        Log.d(TAG, "Navigation STOPPED.");
        isNavigating.setValue(false);
    }

    /**
     * clearError()
     *
     * Called by the UI after displaying the error (e.g. after
     * showing a Toast). Prevents the same error re-appearing
     * after a screen rotation.
     */
    public void clearError() {
        errorMessage.setValue(null);
    }

    // ============================================================
    // READ-ONLY LIVEDATA GETTERS — exposed to UI
    // Returning LiveData (not MutableLiveData) so only this
    // ViewModel can write; the UI can only observe.
    // ============================================================

    public LiveData<List<Place>> getSearchResults() { return searchResults; }
    public LiveData<Place>       getSelectedPlace()  { return selectedPlace; }
    public LiveData<Boolean>     getIsNavigating()   { return isNavigating; }
    public LiveData<String>      getErrorMessage()   { return errorMessage; }
    public LiveData<Boolean>     getIsLoading()      { return isLoading; }
}
