package com.example.myspps.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myspps.R;
import com.example.myspps.models.Place;
import com.example.myspps.utils.NavigationManager;
import com.example.myspps.viewmodel.AppViewModel;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";

    private AppViewModel viewModel;
    private Place        destination;

    // Views
    private TextView tvDestName;
    private TextView tvDestRoom;
    private TextView tvDestDesc;
    private Button   btnStartNav;
    private Button   btnStopNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // ── Toolbar ───────────────────────────────────────────
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Navigate");
        }

        // ── Bind Views ────────────────────────────────────────
        tvDestName  = findViewById(R.id.tvDestName);
        tvDestRoom  = findViewById(R.id.tvDestRoom);
        tvDestDesc  = findViewById(R.id.tvDestDesc);
        btnStartNav = findViewById(R.id.btnStartNav);
        btnStopNav  = findViewById(R.id.btnStopNav);

        destination = NavigationManager.extractPlaceFromIntent(getIntent());

        if (destination == null) {
            // Safety check — should never happen in normal flow
            Toast.makeText(this, "Error: No destination selected.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Destination is null — finishing MapActivity.");
            finish();
            return;
        }

        Log.d(TAG, "MapActivity opened for: " + destination.getName());

        // ── Populate Destination Info Panel ───────────────────
        tvDestName.setText(destination.getName());
        tvDestRoom.setText("Room " + destination.getRoomNumber()
                + "  •  Floor " + destination.getFloorNumber());
        tvDestDesc.setText(destination.getDescription());

        // ── ViewModel ─────────────────────────────────────────
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        viewModel.selectPlace(destination); // store in shared state

    private void setupButtons() {

        // Start Navigation — tells ViewModel navigation is active
        btnStartNav.setOnClickListener(v -> {
            Log.d(TAG, "Start Navigation tapped.");
            viewModel.startNavigation();
            Toast.makeText(this,
                    "Navigating to " + destination.getName(), Toast.LENGTH_SHORT).show();
            // TODO (Person 3): Start drawing the route + GPS updates here
        });

        // Stop Navigation — tells ViewModel navigation is inactive
        btnStopNav.setOnClickListener(v -> {
            Log.d(TAG, "Stop Navigation tapped.");
            viewModel.stopNavigation();
            Toast.makeText(this, "Navigation stopped.", Toast.LENGTH_SHORT).show();
            // TODO (Person 3): Stop route drawing + GPS tracking here
        });
    }


    private void observeViewModel() {

        // Sync button visibility with navigation state
        viewModel.getIsNavigating().observe(this, isNavigating -> {
            btnStartNav.setEnabled(!isNavigating);
            btnStopNav.setEnabled(isNavigating);
            btnStartNav.setAlpha(isNavigating ? 0.5f : 1.0f);
            btnStopNav.setAlpha(isNavigating ? 1.0f : 0.5f);
            Log.d(TAG, "isNavigating changed: " + isNavigating);
        });

        // Show any error messages
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        // Stop navigation when leaving the map screen
        viewModel.stopNavigation();
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        viewModel.stopNavigation();
        super.onBackPressed();
    }
}
