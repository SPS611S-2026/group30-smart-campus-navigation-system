package com.example.myspps;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myspps.ui.SearchActivity;
import com.example.myspps.utils.NavigationManager;
import com.example.myspps.utils.PermissionManager;
import com.example.myspps.viewmodel.AppViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    /** Shared ViewModel — survives screen rotation */
    private AppViewModel viewModel;

    /**
     * Permission launcher registered before onCreate() finishes.
     * Uses the modern ActivityResult API (Android 8+ compatible).
     */
    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    // ============================================================
    // LIFECYCLE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No layout needed — we immediately navigate to SearchActivity.
        // This activity acts purely as a bootstrap / wiring hub.

        Log.d(TAG, "App starting — bootstrapping all modules.");

        // ----------------------------------------------------------
        // STEP 1: Obtain the shared ViewModel
        // ViewModelProvider guarantees we get the SAME instance
        // even after a screen rotation (no data lost).
        // ----------------------------------------------------------
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        // ----------------------------------------------------------
        // STEP 2: Register the permission launcher
        // Must happen here (before onStart) — cannot be called later.
        // ----------------------------------------------------------
        locationPermissionLauncher = PermissionManager.registerPermissionLauncher(
                this,
                new PermissionManager.PermissionCallback() {

                    @Override
                    public void onLocationPermissionGranted() {
                        Log.d(TAG, "Location permission GRANTED.");
                        Toast.makeText(MainActivity.this,
                                "Location access granted — navigation ready!",
                                Toast.LENGTH_SHORT).show();
                        // Forward to the search screen now that permissions are set
                        goToSearch();
                    }

                    @Override
                    public void onLocationPermissionDenied(boolean isPermanentlyDenied) {
                        Log.w(TAG, "Location permission DENIED. Permanent=" + isPermanentlyDenied);
                        if (isPermanentlyDenied) {
                            PermissionManager.showPermanentlyDeniedDialog(MainActivity.this);
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Location permission denied — navigation will not work.",
                                    Toast.LENGTH_LONG).show();
                        }
                        // Still go to search so the app doesn't get stuck
                        goToSearch();
                    }
                }
        );

        // ----------------------------------------------------------
        // STEP 3: Observe global error messages
        // Any module (search, repo, viewmodel) can post an error.
        // We catch it here and show a Toast.
        // ----------------------------------------------------------
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Log.w(TAG, "Global error: " + error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        // ----------------------------------------------------------
        // STEP 4: Handle permissions then navigate
        // ----------------------------------------------------------
        initPermissions();
    }

    // ============================================================
    // PRIVATE HELPERS
    // ============================================================

    /**
     * initPermissions()
     *
     * Three possible branches:
     *   A) Already granted → skip prompt, go to search
     *   B) Denied before   → show rationale dialog first
     *   C) First launch    → ask directly
     */
    private void initPermissions() {
        if (PermissionManager.checkLocationPermission(this)) {
            Log.d(TAG, "Permission already granted — skipping prompt.");
            goToSearch();
        } else if (PermissionManager.shouldShowRationale(this)) {
            Log.d(TAG, "Showing rationale dialog before permission request.");
            PermissionManager.showPermissionRationaleDialog(this, () ->
                    locationPermissionLauncher.launch(PermissionManager.LOCATION_PERMISSIONS)
            );
        } else {
            Log.d(TAG, "Requesting location permission (first time).");
            locationPermissionLauncher.launch(PermissionManager.LOCATION_PERMISSIONS);
        }
    }

    /**
     * goToSearch()
     *
     * Launches SearchActivity and finishes MainActivity so the
     * user cannot press Back and land on a blank screen.
     */
    private void goToSearch() {
        NavigationManager.goToSearch(this);
        finish(); // Remove MainActivity from the back stack
    }
}
