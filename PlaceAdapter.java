package com.example.myspps.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myspps.R;
import com.example.myspps.models.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
    }

    // ── Fields ────────────────────────────────────────────────

    /** Current list of places to display */
    private List<Place> places = new ArrayList<>();

    /** Called when the user taps a row */
    private final OnPlaceClickListener listener;

    // ── Constructor ───────────────────────────────────────────

    public PlaceAdapter(OnPlaceClickListener listener) {
        this.listener = listener;
    }

    // ── Data Update ───────────────────────────────────────────

    public void setPlaces(List<Place> places) {
        this.places = places != null ? places : new ArrayList<>();
        notifyDataSetChanged();
    }

    // ── RecyclerView.Adapter Overrides ────────────────────────

    /**
     * onCreateViewHolder()
     * Called once per visible row (or until the pool is full).
     * Inflates item_place.xml to create the row's View tree,
     * then wraps it in a PlaceViewHolder for fast field access.
     */
    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater converts XML → View objects
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(itemView);
    }

    /**
     * onBindViewHolder()
     * Called every time a ViewHolder is about to become visible
     * (including when recycled). We fill its TextViews with the
     * data for the Place at the given list position.
     */
    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = places.get(position);

        holder.tvName.setText(place.getName());
        holder.tvRoom.setText("Room: " + place.getRoomNumber()
                + "  •  Floor: " + place.getFloorNumber());
        holder.tvDesc.setText(place.getDescription());

        // Wire the tap listener — passes the Place back to ResultsActivity
        holder.itemView.setOnClickListener(v -> listener.onPlaceClick(place));
    }

    /** Total number of items in the current list */
    @Override
    public int getItemCount() {
        return places.size();
    }

    // ── ViewHolder ────────────────────────────────────────────

    /**
     * PlaceViewHolder
     *
     * Holds direct references to the TextViews inside one row.
     * WITHOUT a ViewHolder, we'd call findViewById() on every
     * bind — that's an expensive tree traversal each time.
     * WITH a ViewHolder, we look up the views ONCE and reuse
     * the references as the rows are recycled.
     */
    static class PlaceViewHolder extends RecyclerView.ViewHolder {

        final TextView tvName; // Place name
        final TextView tvRoom; // "Room 101 · Floor 1"
        final TextView tvDesc; // Short description

        PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPlaceName);
            tvRoom = itemView.findViewById(R.id.tvPlaceRoom);
            tvDesc = itemView.findViewById(R.id.tvPlaceDesc);
        }
    }
}
