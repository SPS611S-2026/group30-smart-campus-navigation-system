package com.example.myspps.ui;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myspps.R;
import com.example.myspps.utils.NavigationManager;
import com.example.myspps.viewmodel.AppViewModel;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private AppViewModel viewModel;
    private EditText     etSearch;
    private Button       btnSearch;

    // ============================================================
    // LIFECYCLE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // ── Bind Views ────────────────────────────────────────
        // findViewById connects our Java variables to the XML widgets
        etSearch  = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);

        // ── Obtain Shared ViewModel ───────────────────────────
        // ViewModelProvider returns the SAME AppViewModel instance
        // used throughout the whole app (shared state).
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        // ── Observe LiveData ──────────────────────────────────
        observeViewModel();

        // ── Set Up User Interactions ──────────────────────────
        setupInteractions();
    }

    // ============================================================
    // LIVEDATA OBSERVERS
    // React to data changes without manually polling.
    // ============================================================

    private void observeViewModel() {

        // When search results are ready, navigate to ResultsActivity.
        // We check size > 0 here to avoid navigating on empty results
        // (the error message LiveData handles the empty-result toast).
        viewModel.getSearchResults().observe(this, results -> {
            if (results != null && !results.isEmpty()) {
                // Results are available — go show them
                String query = etSearch.getText().toString().trim();
                NavigationManager.goToResults(this, query);
            }
        });

        // Show any error messages (e.g. "No locations found")
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        // Toggle the Search button enabled/disabled while loading.
        // Prevents double-tapping the search button.
        viewModel.getIsLoading().observe(this, isLoading -> {
            btnSearch.setEnabled(!isLoading);
            btnSearch.setText(isLoading ? "Searching…" : "Search");
        });
    }

    // ============================================================
    // USER INTERACTIONS
    // ============================================================

    private void setupInteractions() {

        // ── Search Button Click ───────────────────────────────
        btnSearch.setOnClickListener(v -> performSearch());

        // ── Keyboard "Search" / "Done" Action ─────────────────
        // When the user presses the Search key on the keyboard
        // (instead of tapping the button), we trigger the same action.
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true; // consumed the event
            }
            return false;
        });
    }

    /**
     * performSearch()
     *
     * Reads the query, hides the keyboard, and delegates to the
     * ViewModel. The ViewModel result is observed above —
     * we do NOT navigate here directly; we wait for LiveData.
     *
     * This keeps the Activity "dumb": it only handles input/output,
     * never business logic.
     */
    private void performSearch() {
        String query = etSearch.getText().toString().trim();

        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search term.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hide the soft keyboard so the results screen isn't obstructed
        hideKeyboard();

        // Delegate to ViewModel (Person 4 → Person 1 chain)
        viewModel.performSearch(query);
    }

    /** Programmatically dismiss the on-screen keyboard */
    private void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
