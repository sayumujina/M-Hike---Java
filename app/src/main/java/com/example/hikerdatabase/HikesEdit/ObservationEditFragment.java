package com.example.hikerdatabase.HikesEdit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hikerdatabase.DatabaseHelper.DatabaseHelper;

import com.example.hikerdatabase.HikesInfo.Hike;
import com.example.hikerdatabase.HikesInfo.HikeListActivity;
import com.example.hikerdatabase.ObservationsInfo.Observation;
import com.example.hikerdatabase.ObservationsInfo.ObservationAdapter;
import com.example.hikerdatabase.R;


import java.util.ArrayList;

public class ObservationEditFragment extends Fragment {
    RecyclerView observationsRecyclerView;
    ObservationAdapter observationAdapter;
    DatabaseHelper dbHelper;
    ArrayList<Observation> observations;

    private HikeEditViewModel hikeEditViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_observation_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the hike data from the shared view model
        hikeEditViewModel = new ViewModelProvider(requireActivity()).get(HikeEditViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(getContext());

        // Add mock observations
        // FOR TESTING PURPOSES ONLY
        // addMockObservations();


        // Get the RecyclerView from the layout
        observationsRecyclerView = view.findViewById(R.id.observationsRecyclerView);
        observationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        observationsRecyclerView.requestLayout();

        // Retrieve observations from a hike and send it to the adapter
        observations = dbHelper.getObservationsFromHike(HikeEditActivity.getEditHikeId());
        Log.d("ObservationEditFragment", "Retrieved " + observations.size() + " observations for hike ID: " + HikeEditActivity.getEditHikeId());
        observationAdapter = new ObservationAdapter(observations);
        observationsRecyclerView.setAdapter(observationAdapter);

        // Attach the add button listener
        view.findViewById(R.id.addObservationButton).setOnClickListener(this::onAddObservationClick);

        // Attach the save button listener
        view.findViewById(R.id.saveHikeButton).setOnClickListener(this::onSaveObservationsClick);

        setUpAdapterListener();
    }

    // Handle add button click to add a new observation
    private void onAddObservationClick(View view) {
        // Log.d("ObservationEditFragment", "Add Observation button clicked");

        // Use the id of the hike being edited if there's any
        Log.d("ObservationEditFragment", "Adding observation to new hike (no ID yet)");
        observations.add(new Observation(-1, HikeEditActivity.getEditHikeId(), "", "", "", ""));

        // Refresh the adapter
        observationAdapter.notifyItemInserted(observations.size() - 1);
    }

    // Handle per observation delete button click from the adapter
    private void setUpAdapterListener() {
        observationAdapter.setOnObservationRemovedListener( (observation, position) -> {
            Log.d("ObservationEditFragment", "Observation removed at position: " + position);

            // Remove from the observations list
            observations.remove(position);

            // Refresh the adapter
            observationsRecyclerView.setAdapter(observationAdapter);
        });
    }

    // Handle save button click to save the entire hike to the database
    private void onSaveObservationsClick(View view) {
        Log.d("ObservationEditFragment", "Save Observations button clicked");

        // Iterate through each observation in the RecyclerView
        // Only proceed if there are observations
        if (observationsRecyclerView.getChildCount() == 0) {
            Log.d("ObservationEditFragment", "No observations to save");
            showConfirmationDialog(view);
            return;
        }

        // Check if all required fields are filled
        for (int i = 0; i < observationsRecyclerView.getChildCount(); i++) {
            if (!validateRequiredObservationFields(i)) {
                return;
            }
        }

        showConfirmationDialog(view);
    }

    // Check for empty required fields and invalid data
    private boolean validateRequiredObservationFields(int position) {
        View observationView = observationsRecyclerView.getChildAt(position);

        EditText editName = observationView.findViewById(R.id.templateObservationNameValue);
        EditText editDate = observationView.findViewById(R.id.templateObservationDateValue);
        EditText editTime = observationView.findViewById(R.id.templateObservationTimeValue);

        // Set a flag to avoid returning errors too early
        boolean areAllFieldsFilled = true;

        // Check if required fields are filled
        if (editName.getText().toString().trim().isEmpty()) {
            editName.setError("Name is required");
            areAllFieldsFilled = false;
        } else if (!editName.getText().toString().matches("[a-zA-Z0-9 ]+")) {
            editName.setError("Name contains invalid characters");
            areAllFieldsFilled = false;
        }

        if (editDate.getText().toString().trim().isEmpty()) {
            editDate.setError("Date is required");
            areAllFieldsFilled = false;
        }

        if (editTime.getText().toString().trim().isEmpty()) {
            editTime.setError("Length is required");
            areAllFieldsFilled = false;
        }

        return areAllFieldsFilled;
    }

    // Get the latest data from each item
    private ArrayList<Observation> getAllObservationsFromRecyclerView() {
        ArrayList<Observation> recyclerViewObservationList = new ArrayList<>();

        // Iterate through each observation in the recycler view
        for (int i = 0; i < observationsRecyclerView.getChildCount(); i++) {
            View observationViewHolder = observationsRecyclerView.getChildAt(i);
            if (observationViewHolder != null) {
                recyclerViewObservationList.add(new Observation(
                        observations.get(i).getObservationId(),
                        HikeEditActivity.getEditHikeId(),
                        ((EditText) observationViewHolder.findViewById(R.id.templateObservationNameValue)).getText().toString(),
                        ((EditText) observationViewHolder.findViewById(R.id.templateObservationDateValue)).getText().toString(),
                        ((EditText) observationViewHolder.findViewById(R.id.templateObservationTimeValue)).getText().toString(),
                        ((EditText) observationViewHolder.findViewById(R.id.templateObservationCommentsValue)).getText().toString()
                ));
            }
        }
        return recyclerViewObservationList;
    }

    // Show confirmation dialog before saving
    private void showConfirmationDialog(View view) {
        Hike hikeDetails = hikeEditViewModel.getHikeDetailsData().getValue();

        if (hikeDetails == null) {
            Log.e("ObservationEditFragment", "Hike details are null");
            Toast.makeText(requireContext(), "Error: Hike details are missing.", Toast.LENGTH_LONG).show();
            return;
        }

        // Collect all the data and build the confirmation message
        String name = hikeDetails.getName();
        String location = hikeDetails.getLocation();
        String date = hikeDetails.getDate();
        double length = hikeDetails.getLength();
        String isParkingAvailable = hikeDetails.getParkingAvailability();
        int difficulty = hikeDetails.getDifficulty();

        StringBuilder confirmationMessage = new StringBuilder();
        confirmationMessage.append("Name: ").append(name).append("\n");
        confirmationMessage.append("Location: ").append(location).append("\n");
        confirmationMessage.append("Date: ").append(date).append("\n");
        confirmationMessage.append("Length: ").append(length).append("\n");
        confirmationMessage.append("Parking Available: ").append(isParkingAvailable).append("\n");
        confirmationMessage.append("Difficulty: ").append(difficulty).append("\n");

        String[] hikeMembers = hikeDetails.getHikeMembers();
        // Only proceed if there are hike members
        if (hikeMembers.length > 0) {
            StringBuilder hikeMembersText = new StringBuilder();
            for (String member : hikeMembers) {
                if (!member.isEmpty()) {
                    hikeMembersText.append(member).append(", ");
                }
            }
            confirmationMessage.append("Hike Members: ").append(hikeMembersText).append("\n");
        }

        String[] gears = hikeDetails.getGears();
        // Only proceed if there are gears
        if (gears.length > 0) {
            StringBuilder gearText = new StringBuilder();
            for (String gear : gears) {
                if (!gear.isEmpty()) {
                    gearText.append(gear).append(", ");
                }
            }
            confirmationMessage.append("Gear: ").append(gearText).append("\n");
        }

        String description = hikeDetails.getDescription();
        if (!description.isEmpty()) {
            confirmationMessage.append("Description: ").append(description).append("\n");
        }

        confirmationMessage.append("Number of Observations: ").append(observations.size()).append("\n");

        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirm Hike Details")
                .setMessage(confirmationMessage.toString())
                .setPositiveButton("Save", (dialog, which) -> {
                    performActualSave(view);
                })
                .setNegativeButton("Edit", null)
                .show();
    }

    // Perform the actual save operation
    private void performActualSave(View view) {
        Context context = view.getContext();
        try (DatabaseHelper databaseHelper = new DatabaseHelper(context)) {
            Hike hikeDetails = hikeEditViewModel.getHikeDetailsData().getValue();

            if (hikeDetails == null) {
                Log.e("ObservationEditFragment", "Hike details are null");
                Toast.makeText(requireContext(), "Error: Hike details are missing.", Toast.LENGTH_LONG).show();
                return;
            }

            // Collect all the data
            String name = hikeDetails.getName();
            String location = hikeDetails.getLocation();
            String date = hikeDetails.getDate();
            double length = hikeDetails.getLength();
            String isParkingAvailable = hikeDetails.getParkingAvailability();
            int difficulty = hikeDetails.getDifficulty();

            String[] hikeMembers = hikeDetails.getHikeMembers();
            // Only proceed if there are hike members
            if (hikeMembers.length > 0) {
                StringBuilder hikeMembersText = new StringBuilder();
                for (String member : hikeMembers) {
                    if (!member.isEmpty()) {
                        hikeMembersText.append(member);
                    }
                }
            }

            String[] gears = hikeDetails.getGears();
            // Only proceed if there are gears
            if (gears.length > 0) {
                StringBuilder gearText = new StringBuilder();
                for (String gear : gears) {
                    if (!gear.isEmpty()) {
                        gearText.append(gear);
                    }
                }
            }

            String description = hikeDetails.getDescription();

            // Save the hike details first to let the observations reference the correct hike ID
            if (HikeEditActivity.getEditMode()) {
                // Update existing hike
                boolean updated = databaseHelper.updateHikeDetails(
                        HikeEditActivity.getEditHikeId(), name, location, date, isParkingAvailable,
                    length, difficulty, hikeMembers, gears, description
                );

                if (updated) {
                    Toast.makeText(context, "Hike updated successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Failed to update hike!", Toast.LENGTH_LONG).show();
                }
            } else {
                // Create new hike
                databaseHelper.insertHikeDetails(
                        name, location, date, isParkingAvailable,
                        length, difficulty, hikeMembers, gears, description
                );
                Toast.makeText(context, "New hike created successfully!", Toast.LENGTH_LONG).show();
            }

            // Retrieve the newest data from the adapter
            ArrayList<Observation> currentObservations = getAllObservationsFromRecyclerView();

            // IF an observation is removed from the recycler view, delete it from the database
            for (Observation observation : dbHelper.getObservationsFromHike(HikeEditActivity.getEditHikeId())) {
                // Check for observation Id existence
                if (currentObservations.stream().noneMatch(o -> o.getObservationId() == observation.getObservationId())) {
                    Log.d("ObservationEditFragment", "Deleting observation ID: " + observation.getObservationId());
                    databaseHelper.deleteObservationById(observation.getObservationId());
                }
            }

            // Check whether to save or update observations
            for (Observation observation : currentObservations) {
                // Update existing observation if observation id exists
                if (observation.getObservationId() != -1) {
                    Log.d("ObservationEditFragment", "Updating observation ID: " + observation.getHikeId());
                    boolean updated = databaseHelper.updateObservation(
                            observation.getObservationId(),
                            observation.getHikeId(),
                            observation.getObservationName(),
                            observation.getObservationDate(),
                            observation.getObservationTime(),
                            observation.getObservationComments()
                    );
                    if (!updated) {
                        Log.e("ObservationEditFragment", "Failed to update observation ID: " + observation.getObservationId());
                    }
                }
                // Insert new observation if observation id does not exist
                else {
                    Log.d("ObservationEditFragment", "Inserting new observation for hike ID: " + dbHelper.getLatestHikeId());
                    databaseHelper.insertObservation(
                            dbHelper.getLatestHikeId(),
                            observation.getObservationName(),
                            observation.getObservationDate(),
                            observation.getObservationTime(),
                            observation.getObservationComments()
                    );
                }
            }

            // Now save the observations
            Toast.makeText(context, "Observations saved successfully!", Toast.LENGTH_LONG).show();

            // Move to the hike list
            Intent detailsIntent = new Intent(context, HikeListActivity.class);
            startActivity(detailsIntent);
            requireActivity().finish();


        } catch (Exception e) {
            Toast.makeText(context, "Error saving hike: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    // Add mock observations
    private void addMockObservations() {
        dbHelper = new DatabaseHelper(getContext());

        String[] mockNames = {"Bird Sighting", "Waterfall", "Interesting Rock Formation", "Wildflower Patch", "Scenic Overlook"};
        String[] mockComments = {"Spotted a rare species of bird.", "Beautiful waterfall with a rainbow.", "Unique rock shapes.", "Field full of colorful wildflowers.", "Breathtaking view of the valley."};
        String mockDate = "2024-06-15";
        String[] mockTime = {"10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM"};
        int[] mockHikeId = {1, 2, 3, 4 , 5};
        int[] anotherMockHikeId = {2, 3, 4, 5, 1};
        for (int i = 0; i < mockNames.length; i++) {
            dbHelper.insertObservation(mockHikeId[i], mockNames[i], mockDate, mockTime[i], mockComments[i]);
        }
        for (int i = 0; i < mockNames.length; i++) {
            dbHelper.insertObservation(anotherMockHikeId[i], mockNames[i], mockDate, mockTime[i], mockComments[i]);
        }
    }
}
