package com.example.hikerdatabase.HikesEdit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hikerdatabase.HikesInfo.Hike;
import com.example.hikerdatabase.DatabaseHelper.DatabaseHelper;

import com.example.hikerdatabase.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.Slider;

public class HikeEditFragment extends Fragment {
    private boolean isEditMode;
    private int hikeIdToEdit;
    EditText editName;
    EditText editLocation;
    EditText editDate;
    EditText editLength;
    MaterialButtonToggleGroup editParking;
    Slider editDifficulty;
    EditText editDescription;
    Button editObservationButton;

    private HikeEditViewModel hikeEditViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_hikes_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.hikeEditLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Update the hike details initially
        hikeEditViewModel = new ViewModelProvider(requireActivity()).get(HikeEditViewModel.class);

        // Initialise views
        editName = view.findViewById(R.id.editName);
        editLocation = view.findViewById(R.id.editLocation);
        editDate = view.findViewById(R.id.editDate);
        editLength = view.findViewById(R.id.editLength);
        editParking = view.findViewById(R.id.hikeParkingAvailabilityToggleGroup);
        editDifficulty = view.findViewById(R.id.difficultySlider);
        editDescription = view.findViewById(R.id.editDescription);

        // Initialise necessary components
        HikeEditActivity.setupDateSelection(editDate);
        setupParkingToggleGroup();
        setupDynamicFields(view);

        isEditMode = HikeEditActivity.getEditMode();
        hikeIdToEdit = HikeEditActivity.getEditHikeId();
        Log.d("HikeEditFragment", "isEditMode: " + isEditMode + ", editHikeId: " + hikeIdToEdit);
        // Check if this is edit mode
        if (isEditMode) {
            loadExistingData(view);
        }

        // Remove error prompts when user starts editing
        editName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                removeMissingFieldPrompts();
            }
        });
        editLocation.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                removeMissingFieldPrompts();
            }
        });
        editDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                removeMissingFieldPrompts();
            }
        });
        editLength.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                removeMissingFieldPrompts();
            }
        });

        // Set up save button listener
        // Switch to the observation tab
        editObservationButton = view.findViewById(R.id.editObservationButton);
        editObservationButton.setOnClickListener(v -> {
            // Validate required fields before switching
            if (validateRequiredFields()) {
                switchToObservationTabListener(requireView());
            } else {
                Toast.makeText(requireContext(), "Please fill in all required fields.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Save data when fragment is paused, which is when switching tabs
    // Basically this is to not allow saves when required fields are not filled
    // I did not just disallow switching tabs because maybe the user wants to edit the observation first
    // The edit observation button kinda does the opposite but I thought there should be a way to let the user SEES the missing fields
    @Override
    public void onPause() {
        super.onPause();
        if (validateRequiredFields()) {
            switchToObservationTabListener(requireView());
        } else {
            Toast.makeText(requireContext(), "Please fill in all required fields.", Toast.LENGTH_LONG).show();
        }

    }

    // Remove all missing field prompts
    private void removeMissingFieldPrompts() {
        editName.setError(null);
        editLocation.setError(null);
        editDate.setError(null);
        editLength.setError(null);
    }

    // Switch to observation tab listener
    private void switchToObservationTabListener(View view) {
        // Switch to the observation tab
        HikeEditActivity activity = (HikeEditActivity) requireActivity();
        ViewPager2 viewPager = activity.findViewById(R.id.viewPager);
        viewPager.setCurrentItem(1);

        // Send the current hike details data to the ViewModel
        hikeEditViewModel.setHikeDetailsData(getCurrentHikeDetails(view));

    }
    // Load existing data if in edit mode
    private void loadExistingData(View view) {
        // Load hike data from database
        try (DatabaseHelper databaseHelper = new DatabaseHelper(requireContext())) {
            final Hike hike = databaseHelper.getHikeById(hikeIdToEdit);
            if (hike != null) {
                editName.setText(hike.getName());
                editLocation.setText(hike.getLocation());
                editDate.setText(hike.getDate());
                editLength.setText(String.valueOf(hike.getLength()));
                editDescription.setText(hike.getDescription());

                // Set the parking availability toggle
                editParking.check(hike.getParkingAvailability().equals("Yes") ?
                        R.id.hikeParkingAvailableButton : R.id.hikeParkingUnavailableButton);

                // Set the difficulty slider value
                HikeEditActivity.setSliderValue(editDifficulty, hike.getDifficulty());

                // Load dynamic fields (members and gear)
                loadDynamicFields(view, hike);

                // Scrolls back to the top
                ScrollView scrollView = view.findViewById(R.id.hikeEditScrollView);
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_UP));
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error loading hike data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("HikeEditFragment", "Error loading hike data: " + e.getMessage());
        }
    }

    // Load dynamic fields for hike members and gear if in edit mode
    private void loadDynamicFields(View view, Hike hike) {
        LinearLayout hikeMembersContainer = view.findViewById(R.id.hikeMembersContainer);
        LinearLayout gearContainer = view.findViewById(R.id.gearsContainer);

        // Clear existing views
        hikeMembersContainer.removeAllViews();
        gearContainer.removeAllViews();

        // Populate hike members and gears
        String[] members = hike.getHikeMembers();
        if (members != null) {
            for (String member : members) {
                if (member != null && !member.isEmpty()) {
                    HikeEditActivity.addDynamicField(view, R.id.hikeMembersContainer,
                            R.layout.activity_dynamic_field_layout,"Enter hike member", member);
                }
            }
        }

        String[] gearItems = hike.getGears();
        if (gearItems != null) {
            for (String gear : gearItems) {
                if (gear != null && !gear.isEmpty()) {
                    HikeEditActivity.addDynamicField(view, R.id.gearsContainer,
                            R.layout.activity_dynamic_field_layout,"Enter gear", gear);
                }
            }
        }
    }

    // Set entries for parking
    private void setupParkingToggleGroup() {
        // Default selection
        editParking.check(R.id.hikeParkingAvailableButton);
    }

    private void setupDynamicFields(View view) {
        // Setup add button for hike members
        ImageButton addMemberButton = view.findViewById(R.id.addHikeMemberButton);
        addMemberButton.setOnClickListener(v -> HikeEditActivity.addDynamicField(view, R.id.hikeMembersContainer,
                R.layout.activity_dynamic_field_layout, "Enter hike member", null));

        // Setup add button for gear
        ImageButton addGearButton = view.findViewById(R.id.addGearButton);
        addGearButton.setOnClickListener(v -> HikeEditActivity.addDynamicField(view, R.id.gearsContainer,
                R.layout.activity_dynamic_field_layout,"Enter gear", null));
    }

    // Get the current hike details from the form
    private Hike getCurrentHikeDetails(View view) {
        // Collect all the data
        String name = editName.getText().toString();
        String location = editLocation.getText().toString();
        String date = editDate.getText().toString();
        double length = Double.parseDouble(editLength.getText().toString());
        String isParkingAvailable = editParking.getCheckedButtonId() == R.id.hikeParkingAvailableButton ? "Yes" : "No";
        int difficulty = (int) editDifficulty.getValue();
        String description = editDescription.getText().toString();

        // Loop through all the items in hike members
        LinearLayout hikeMembersContainer = view.findViewById(R.id.hikeMembersContainer);
        String[] hikeMembers = new String[hikeMembersContainer.getChildCount()];
        for (int i = 0; i < hikeMembersContainer.getChildCount(); i++) {
            View childView = hikeMembersContainer.getChildAt(i);

            // Handle both possible EditText IDs
            EditText memberEditText = childView.findViewById(R.id.editHikeMember);
            if (memberEditText == null) {
                memberEditText = hikeMembersContainer.getChildAt(i).findViewById(R.id.extraEditField);
            }

            hikeMembers[i] = memberEditText.getText().toString();
        }

        // Loop through all the items in gear
        LinearLayout gearsContainer = view.findViewById(R.id.gearsContainer);
        String[] gear = new String[gearsContainer.getChildCount()];
        for (int i = 0; i < gearsContainer.getChildCount(); i++) {
            View childView = gearsContainer.getChildAt(i);

            // Handle both possible EditText IDs
            EditText gearEditText = childView.findViewById(R.id.editGear);
            if (gearEditText == null) {
                gearEditText = gearsContainer.getChildAt(i).findViewById(R.id.extraEditField);
            }
            gear[i] = gearEditText.getText().toString();
        }

        return new Hike(
                hikeIdToEdit,
                name,
                location,
                date,
                isParkingAvailable,
                length,
                difficulty,
                hikeMembers,
                gear,
                description
        );
    }

    // Validate required fields
    // Check for empty required fields and invalid data
    private boolean validateRequiredFields() {
        // Set a flag to avoid returning errors too early
        boolean areAllFieldsFilled = true;

        if (editName.getText().toString().trim().isEmpty()) {
            editName.setError("Name is required");
            areAllFieldsFilled = false;
        } else if (!editName.getText().toString().matches("[a-zA-Z0-9 ]+")) {
            editName.setError("Name contains invalid characters");
            areAllFieldsFilled = false;
        }

        if (editLocation.getText().toString().trim().isEmpty()) {
            editLocation.setError("Location is required");
            areAllFieldsFilled = false;
        }

        if (editDate.getText().toString().trim().isEmpty()) {
            editDate.setError("Date is required");
            areAllFieldsFilled = false;
        }

        if (editLength.getText().toString().trim().isEmpty()) {
            editLength.setError("Length is required");
            areAllFieldsFilled = false;
        }

        return areAllFieldsFilled;
    }

}
