package com.example.hikerdatabase.HikesInfo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hikerdatabase.DatabaseHelper.DatabaseHelper;
import com.example.hikerdatabase.R;

import org.jspecify.annotations.NonNull;

// HikeListViewAdapter class to bind contact data to the RecyclerView
public class HikeListViewAdapter extends RecyclerView.Adapter<HikeListViewAdapter.HikesViewHolder> {
    private final ArrayList<Hike> hikes;
    private boolean selectionMode = false;
    public ArrayList<Integer> selectedHikes = new ArrayList<>();
    public HikeListViewAdapter(ArrayList<Hike> hikes) {
        this.hikes = hikes != null ? hikes : new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return hikes.size();
    }

    // Holds references to the text view
    @NonNull
    @Override
    public HikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_hike_details_template, parent, false);
        return new HikesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HikesViewHolder holder, int position) {
        Log.d("HikeListViewAdapter", "Binding position: " + position);

        Hike hike = hikes.get(position);

        View detailsLayout = holder.itemView.findViewById(R.id.detailedLayout);

        // Initialise basic hike information
        if (hike != null) {
            holder.templateHikeNameValue.setText(hike.getName() != null ? hike.getName() : "");
            holder.templateHikeLocationValue.setText(hike.getLocation() != null ? hike.getLocation() : "");
            holder.templateHikeDateValue.setText(hike.getDate() != null ? hike.getDate() : "");
            holder.templateHikeLengthValue.setText(hike.getLength() != null ? hike.getLength() : "");
            holder.templateHikeDifficultyValue.setText(String.valueOf(hike.getDifficulty()));
            holder.templateHikeParkingValue.setText(hike.getParkingAvailability() != null ? hike.getParkingAvailability() : "");
            holder.templateHikeDescriptionValue.setText(hike.getDescription() != null ? hike.getDescription() : "None");
            holder.templateHikeMembersValue.setText(hike.getHikeMembers() != null ? String.join(", ", hike.getHikeMembers()) : "None");
            holder.templateHikeGearsValue.setText(hike.getGears() != null ? String.join(", ", hike.getGears()) : "None");
        } else {
            holder.templateHikeNameValue.setText("");
            holder.templateHikeLocationValue.setText("");
            holder.templateHikeDateValue.setText("");
            holder.templateHikeLengthValue.setText("");
            holder.templateHikeDifficultyValue.setText("");
            holder.templateHikeParkingValue.setText("");
            holder.templateHikeDescriptionValue.setText("");
            holder.templateHikeMembersValue.setText("");
            holder.templateHikeGearsValue.setText("");
        }

        // Handle selection mode
        holder.itemCheckBox.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
        holder.itemCheckBox.setChecked(selectedHikes.contains(position)); // Set checkbox state based on selection

        holder.itemView.setOnLongClickListener(v -> {
            // Log.d("HikeListViewAdapter", "Long clicked position: " + position);
            if (!selectionMode) {
                enterSelectionMode();
                selectedHikes.add(position);
                holder.itemCheckBox.setChecked(true);
            }

            return true;
        });

        // Handle item selection
        holder.itemView.setOnClickListener(v -> {
            // Log.d("HikeListViewAdapter", "Clicked position: " + position);
            if (selectionMode) {
                if (selectedHikes.contains(position)) {
                    selectedHikes.remove(Integer.valueOf(position));
                    holder.itemCheckBox.setChecked(false);
                } else {
                    selectedHikes.add(position);
                    holder.itemCheckBox.setChecked(true);
                }

                // If all items are selected,notifies the hike list to change "Select All" to "Deselect All"
                // And vice versa
                if (selectedHikes.size() == hikes.size()) {
                    if (hikeSelectionChangeListener != null) {
                        hikeSelectionChangeListener.onHikeSelectionChanged(true);
                    }
                } else {
                    if (hikeSelectionChangeListener != null) {
                        hikeSelectionChangeListener.onHikeSelectionChanged(false);
                    }
                }

            } else {
                // Notify listener for edit request
                if (onHikeClickListener != null) {
                    onHikeClickListener.onHikeClick(hike, position);
                }
            }
        });

        // Handle expand button click
        ImageView expandButton = holder.itemView.findViewById(R.id.expandButton);
        FrameLayout expandLayoutTrigger = holder.itemView.findViewById(R.id.expandLayoutTrigger);
        expandLayoutTrigger.setOnClickListener(v -> {
            expandButton.setRotation(expandButton.getRotation() + 180);
            // Displays other details
            if (detailsLayout.getVisibility() == View.GONE) {
                detailsLayout.setVisibility(View.VISIBLE);
            } else {
                detailsLayout.setVisibility(View.GONE);
            }
        });
    }

    // Listen to checkbox selection changes
    public interface OnHikeSelectionChangeListener {
        void onHikeSelectionChanged(boolean isAllHikesSelected);
    }

    private OnHikeSelectionChangeListener hikeSelectionChangeListener;

    public void setHikeSelectionChangeListener(OnHikeSelectionChangeListener listener) {
        this.hikeSelectionChangeListener = listener;
    }

    // Listen to selection mode changes
    public interface SelectionModeListener {
        void onSelectionModeChanged(boolean isSelectionMode);
    }
    private SelectionModeListener selectionModeListener;
    public void setSelectionModeListener(SelectionModeListener listener) {
        this.selectionModeListener = listener;
    }

    // Enter selection mode
    public void enterSelectionMode() {
        selectionMode = true;
        if (selectionModeListener != null) {
            selectionModeListener.onSelectionModeChanged(true);
        }
        notifyItemRangeChanged(0, getItemCount());
    }

    // Exit selection mode
    public void exitSelectionMode() {
        selectionMode = false;
        selectedHikes.clear();
        if (selectionModeListener != null) {
            selectionModeListener.onSelectionModeChanged(false);
        }
        notifyDataSetChanged();
    }

    // Delete selected hikes from the database
    public void deleteSelectedHikes(DatabaseHelper db) {
        for (int i = 0; i <= selectedHikes.size() - 1; i++) {
            int position = selectedHikes.get(i);
            db.deleteHikeById(hikes.get(position).getId());
        }
        hikes.clear();
        hikes.addAll(db.getAllHikes()); // Refresh the hikes list
        notifyDataSetChanged();
        exitSelectionMode();
    }

    // Listen to edit requests from user when clicking on a hike
    public interface OnHikeClickListener {
        void onHikeClick(Hike hike, int position);
    }
    private OnHikeClickListener onHikeClickListener;
    public void setOnHikeClickListener(OnHikeClickListener listener) {
        this.onHikeClickListener = listener;
    }

    // Select all hikes
    public void selectAllHikes() {
        selectedHikes.clear();
        for (int i = 0; i < hikes.size(); i++) {
            selectedHikes.add(i);
        }
        notifyDataSetChanged();
    }

    // Deselect all hikes
    public void deselectAllHikes() {
        selectedHikes.clear();
        notifyDataSetChanged();
    }

    // HikesViewHolder class to hold the views for each hikes item
    public class HikesViewHolder extends RecyclerView.ViewHolder {
        public CheckBox itemCheckBox;

        public TextView templateHikeNameValue, templateHikeLocationValue, templateHikeDateValue, templateHikeLengthValue,
                templateHikeDifficultyValue, templateHikeParkingValue, templateHikeDescriptionValue, templateHikeMembersValue, templateHikeGearsValue;

        public HikesViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCheckBox = itemView.findViewById(R.id.itemCheckBox);
            //
            templateHikeNameValue = itemView.findViewById(R.id.templateHikeNameValue);
            templateHikeLocationValue = itemView.findViewById(R.id.templateHikeLocationValue);
            templateHikeDateValue = itemView.findViewById(R.id.templateHikeDateValue);
            templateHikeLengthValue = itemView.findViewById(R.id.templateHikeLengthValue);
            templateHikeDifficultyValue = itemView.findViewById(R.id.templateHikeDifficultyValue);
            templateHikeParkingValue = itemView.findViewById(R.id.templateHikeParkingValue);
            templateHikeDescriptionValue = itemView.findViewById(R.id.templateHikeDescriptionValue);
            templateHikeGearsValue = itemView.findViewById(R.id.templateHikeGearsValue);
            templateHikeMembersValue = itemView.findViewById(R.id.templateHikeMembersValue);
        }
    }
}
