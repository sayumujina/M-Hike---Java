package com.example.hikerdatabase.HikesEdit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.content.Intent;

import android.view.MenuItem;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hikerdatabase.DatabaseHelper.DatabaseHelper;
import com.example.hikerdatabase.R;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;
import java.util.Locale;


public class HikeEditActivity extends AppCompatActivity {
    public static boolean isEditMode;
    public static int editHikeId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Set up action bar
        Toolbar toolbar = findViewById(R.id.hikeEditLayoutToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            // Change the back button color to white
            if (toolbar.getNavigationIcon() != null) {
                toolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }

        // Set up the ViewPager with the adapter
        TabLayout tabLayout = findViewById(R.id.hikeEditTabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        HikeEditTabsAdapter adapter = new HikeEditTabsAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach the TabLayout to the ViewPager
        // Switch tabs based on position
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("DETAILS");
            } else {
                tab.setText("OBSERVATIONS");
            }
        }).attach();

        // Re-initialise edit mode and hike id to edit per intent creation
        checkEditMode();
        setEditHikeId();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Read the spinner value and set selection if in edit mode
    public static void setSliderValue(Slider slider, int value) {
        slider.setValue(value);
    }

    // Set up date selection
    public static void setupDateSelection(EditText editDate) {
        editDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            // Getting current year, month and day.
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a DatePickerDialog instance and set it to current date
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    v.getContext(),
                    (view, chosenYear, chosenMonth, chosenDay) -> {
                        editDate.setText(chosenDay + "/" + (chosenMonth + 1) + "/" + chosenYear);
                    },
                    year, month, day);
            // Show the DatePickerDialog
            datePickerDialog.show();
        });
    }

    // Set up time selection
    public static void setupTimeSelection(EditText editTime) {
        editTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);


            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    v.getContext(),
                    (view, chosenHour, chosenMinute) -> {
                        editTime.setText(String.format(Locale.getDefault(), "%02d:%02d", chosenHour, chosenMinute));
                    },
                    hour, minute, true // true for 24-hour view
            );
            timePickerDialog.show();
        });
    }

    // Add a new dynamic field
    public static void addDynamicField(View view, int containerId, int dynamicFieldId, String hint, @Nullable String text) {
        LinearLayout container = view.findViewById(containerId);
        LayoutInflater inflater = LayoutInflater.from(view.getContext());

        // Inflate the new dynamic field layout
        View fieldView = inflater.inflate(dynamicFieldId, container, false);

        // Get the views from the inflated layout
        EditText newField = fieldView.findViewById(R.id.extraEditField);
        ImageButton deleteButton = fieldView.findViewById(R.id.extraDeleteButton);

        // Set properties
        newField.setHint(hint);
        if (text != null) {
            newField.setText(text);
        }

        // Set the delete button listener
        deleteButton.setOnClickListener(v -> container.removeView(fieldView));

        // Add the new view to the container
        container.addView(fieldView);

        newField.requestFocus();
    }

    // Check intent extras to determine edit mode and hike id to edit
    private void checkEditMode() {
        Intent intent = getIntent();
        isEditMode = intent.getExtras() != null && intent.getExtras().getBoolean("isEditMode", false);
    }

    public static boolean getEditMode() {
        return isEditMode;
    }

    private void setEditHikeId() {
        Intent intent = getIntent();
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // If this is a new hike, set to the current index in the database + 1
        if (isEditMode) {
            editHikeId = intent.getExtras() != null ? intent.getExtras().getInt("hikeIdToEdit", -1) : -1;
        }
    }

    public static int getEditHikeId() {
        return editHikeId;
    }
}