package com.example.hikerdatabase.ObservationsInfo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hikerdatabase.HikesEdit.HikeEditActivity;
import com.example.hikerdatabase.R;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder> {
    private static ArrayList<Observation> observationList;

    // Retrieve observations from a specified id
    public ObservationAdapter(ArrayList<Observation> observations) {
        this.observationList = observations != null ? observations : new ArrayList<>();
    }

    @NonNull
    @Override
    public ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_observation_template, parent, false);
        return new ObservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationViewHolder holder, int position) {
        Observation observation = observationList.get(position);
        holder.templateObservationNameValue.setText(observation.getObservationName());
        holder.templateObservationDateValue.setText(observation.getObservationDate());
        holder.templateObservationTimeValue.setText(observation.getObservationTime());
        holder.templateObservationCommentsValue.setText(observation.getObservationComments());

        // Set up date and time pickers
        HikeEditActivity.setupDateSelection(holder.templateObservationDateValue);
        HikeEditActivity.setupTimeSelection(holder.templateObservationTimeValue);

        // Set up delete button listener
        holder.observationDeleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Log.d("ObservationAdapter", "Deleting observation at position: " + currentPosition);

                // Send an event to notify that an observation has been deleted
                if (observationRemovedListener != null) {
                    observationRemovedListener.onObservationRemoved(observationList.get(currentPosition), currentPosition);
                }

                // Remove the item from the list first
                // observationList.remove(currentPosition);

                // Then notify the adapter about the removal
                // notifyItemRemoved(currentPosition);
                // notifyItemRangeChanged(currentPosition, observationList.size());
            }
        });

        // Remove error prompts when user starts editing
        // I genuinely have no idea how to extract this because date and time has 2 layers of parents unlike name
        holder.templateObservationNameValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.templateObservationNameValue.setError(null);
                holder.templateObservationDateValue.setError(null);
                holder.templateObservationTimeValue.setError(null);
            }
        });

        holder.templateObservationDateValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.templateObservationNameValue.setError(null);
                holder.templateObservationDateValue.setError(null);
                holder.templateObservationTimeValue.setError(null);
            }
        });

        holder.templateObservationTimeValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.templateObservationNameValue.setError(null);
                holder.templateObservationDateValue.setError(null);
                holder.templateObservationTimeValue.setError(null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return observationList.size();
    }

    // Sends to observation removed event
    public interface ObservationRemovedListener {
        void onObservationRemoved(Observation observation, int position);
    }

    private ObservationRemovedListener observationRemovedListener;

    public void setOnObservationRemovedListener(ObservationRemovedListener listener) {
        this.observationRemovedListener = listener;
    }

    public static class ObservationViewHolder extends RecyclerView.ViewHolder {
        EditText templateObservationNameValue, templateObservationDateValue, templateObservationTimeValue, templateObservationCommentsValue;
        ImageButton observationDeleteButton;
        public ObservationViewHolder(@NonNull View itemView) {
            super(itemView);
            templateObservationNameValue = itemView.findViewById(R.id.templateObservationNameValue);
            templateObservationDateValue = itemView.findViewById(R.id.templateObservationDateValue);
            templateObservationTimeValue = itemView.findViewById(R.id.templateObservationTimeValue);
            templateObservationCommentsValue = itemView.findViewById(R.id.templateObservationCommentsValue);
            observationDeleteButton = itemView.findViewById(R.id.observationDeleteButton);
        }
    }
}
