package com.example.myspps.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myspps.R;
import com.example.myspps.models.Place;
import com.example.myspps.utils.NavigationManager;
import com.example.myspps.viewmodel.AppViewModel;


public class ResultsActivity extends AppCompatActivity {

    private AppViewModel viewModel;
    private RecyclerView rvResults;
    private TextView     tvResultsTitle;
    private TextView     tvNoResults;
    private ProgressBar  progressBar;
    private PlaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // ── Bind Views ────────────────────────────────────────
        rvResults      = findViewById(R.id.rvResults);
        tvResultsTitle = findViewById(R.id.tvResultsTitle);
        tvNoResults    = findViewById(R.id.tvNoResults);
        progressBar    = findViewById(R.id.progressBar);

        // ── Back button in toolbar ────────────────────────────
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search Results");
        }

        // ── ViewModel ─────────────────────────────────────────
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        // ── RecyclerView Setup ────────────────────────────────
        // LinearLayoutManager = vertical scrolling list
        // PlaceAdapter handles rendering each row
        adapter = new PlaceAdapter(place -> {
            // This lambda is called when a row is tapped
            viewModel.selectPlace(place);                  // store selection
            NavigationManager.goToMap(this, place);        // open map
        });
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        // ── Read query from Intent ────────────────────────────
        // SearchActivity passed the query via NavigationManager.goToResults()
        String query = getIntent().getStringExtra(NavigationManager.EXTRA_SEARCH_QUERY);
        if (query != null) {
            tvResultsTitle.setText("Results for: \"" + query + "\"");
            // Re-run search in case ViewModel state was cleared
            viewModel.performSearch(query);
        }

        // ── Observe LiveData ──────────────────────────────────
        observeViewModel();
    }

    private void observeViewModel() {

        // When search results arrive, hand them to the RecyclerView adapter
        viewModel.getSearchResults().observe(this, results -> {
            if (results == null || results.isEmpty()) {
                // Show "no results" message, hide list
                rvResults.setVisibility(View.GONE);
                tvNoResults.setVisibility(View.VISIBLE);
            } else {
                // Show list, hide "no results" message
                rvResults.setVisibility(View.VISIBLE);
                tvNoResults.setVisibility(View.GONE);
                adapter.setPlaces(results); // update RecyclerView
            }
        });

        // Show/hide loading spinner while search runs
        viewModel.getIsLoading().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        // Show errors as Toast
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });
    }

    // Handles the toolbar back-arrow press
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // go back to SearchActivity
        return true;
    }
}
