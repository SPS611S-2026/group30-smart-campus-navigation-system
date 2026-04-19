package com.example.myspps.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    private static final String TAG = "PermissionManager";

    /** Array of location permissions we request together */
    public static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    // ── Callback Interface ────────────────────────────────────

    /**
     * Implemented by the calling Activity.
     * Called when the user makes a permission decision.
     */
    public interface PermissionCallback {
        void onLocationPermissionGranted();
        /**
         * @param isPermanentlyDenied TRUE if the user ticked
         *        "Don't ask again" — we can no longer prompt,
         *        we must direct them to Settings instead.
         */
        void onLocationPermissionDenied(boolean isPermanentlyDenied);
    }

    // ── Static Helpers ────────────────────────────────────────

    /**
     * checkLocationPermission()
     * Returns TRUE if FINE location is already granted.
     * Use this on startup to skip the permission prompt for
     * returning users who already accepted.
     */
    public static boolean checkLocationPermission(Context context) {
        boolean granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "Location permission pre-check: " + granted);
        return granted;
    }

    /**
     * shouldShowRationale()
     * Returns TRUE if the user denied once before (but didn't
     * select "Don't ask again"). In this case we should show a
     * friendly explanation BEFORE requesting again.
     */
    public static boolean shouldShowRationale(Activity activity) {
        return activity.shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * showPermissionRationaleDialog()
     * Displays a dialog explaining WHY we need location access.
     * Improves grant rate after an initial denial.
     * onProceed is called when the user clicks "Allow" — the
     * caller then fires the actual system permission request.
     */
    public static void showPermissionRationaleDialog(
            AppCompatActivity activity, Runnable onProceed) {

        new AlertDialog.Builder(activity)
                .setTitle("Location Access Needed")
                .setMessage("NUST Smart Navigation needs your location to:\n\n"
                        + "• Show your position on the campus map\n"
                        + "• Calculate routes to your destination\n"
                        + "• Provide real-time navigation guidance\n\n"
                        + "Your location is used only while navigating.")
                .setPositiveButton("Allow Location", (d, w) -> {
                    Log.d(TAG, "Rationale accepted — requesting permission.");
                    onProceed.run();
                })
                .setNegativeButton("Not Now", (d, w) -> {
                    Log.w(TAG, "Rationale declined by user.");
                    d.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    /**
     * showPermanentlyDeniedDialog()
     * When the user selected "Don't ask again", the system will
     * silently reject any future runtime requests. The only way
     * to grant the permission is via the Settings app.
     * This dialog guides the user there.
     */
    public static void showPermanentlyDeniedDialog(AppCompatActivity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Permission Required")
                .setMessage("Location permission was permanently denied.\n\n"
                        + "To enable navigation:\n"
                        + "Settings → Apps → MySPPS → Permissions → Location → Allow")
                .setPositiveButton("Open Settings", (d, w) -> {
                    android.content.Intent i = new android.content.Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(android.net.Uri.fromParts(
                            "package", activity.getPackageName(), null));
                    activity.startActivity(i);
                })
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .show();
    }

    public static ActivityResultLauncher<String[]> registerPermissionLauncher(
            AppCompatActivity activity, PermissionCallback callback) {

        return activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                results -> {
                    Boolean fine   = results.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarse = results.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (Boolean.TRUE.equals(fine) || Boolean.TRUE.equals(coarse)) {
                        Log.d(TAG, "Permission GRANTED.");
                        callback.onLocationPermissionGranted();
                    } else {
                        boolean permanent = !activity.shouldShowRequestPermissionRationale(
                                Manifest.permission.ACCESS_FINE_LOCATION);
                        Log.w(TAG, "Permission DENIED. Permanent=" + permanent);
                        callback.onLocationPermissionDenied(permanent);
                    }
                });
    }
}
