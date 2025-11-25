package com.example.hikerdatabase.HikesInfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hikerdatabase.DatabaseHelper.DatabaseHelper;
import com.example.hikerdatabase.HikesEdit.HikeEditActivity;
import com.example.hikerdatabase.R;

import java.util.ArrayList;
import java.util.List;

public class HikeListActivity extends AppCompatActivity {
    RecyclerView.Adapter hikeListViewAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    LinearLayout optionsBar;
    SearchView searchView;
    DatabaseHelper databaseHelper;
    ArrayList<Hike> allHikes;
    Button selectAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Delete the database file completely
        // FOR TESTING ONLY
        // this.deleteDatabase("hikes_database");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hikes_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.hikeRecyclerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up action bar
        Toolbar toolbar = findViewById(R.id.hikeListLayoutToolbar);
        setSupportActionBar(toolbar);

        // Get the RecyclerView from the layout
        recyclerView = findViewById(R.id.hikeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.requestLayout();

        // Get the contact details from the database and displays it
        databaseHelper = new DatabaseHelper(this);

        // Add test data
        // FOR TESTING ONLY
        // addTestData(databaseHelper);

        // Retrieve all hikes and send it to the adapter
        ArrayList<Hike> hikes = databaseHelper.getAllHikes();
        hikeListViewAdapter = new HikeListViewAdapter(hikes);
        recyclerView.setAdapter(hikeListViewAdapter);

        allHikes = databaseHelper.getAllHikes();

        // Setup search functionality
        setupSearchView();

        // Setup existing functionality
        setupOptionsBar();
        setupClickListeners(databaseHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hike_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.switchToAddHikeViewButton) {
            Intent intent = new Intent(this, HikeEditActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Update RecyclerView per search
    private void updateRecyclerView(List<Hike> hikes) {
        hikeListViewAdapter = new HikeListViewAdapter(new ArrayList<>(hikes));
        recyclerView.setAdapter(hikeListViewAdapter);

        // Re-setup listeners for the new adapter
        setupAdapterListeners();
    }

    // Set up the options bar for selection mode
    private void setupOptionsBar() {
        optionsBar = findViewById(R.id.optionsBar);
    }

    // Set up click listeners
    private void setupClickListeners(DatabaseHelper databaseHelper) {
        setupAdapterListeners();

        // Return button
        ImageView returnButton = findViewById(R.id.returnIcon);
        returnButton.setOnClickListener(v -> {
            ((HikeListViewAdapter) hikeListViewAdapter).exitSelectionMode();
        });

        // Select all button
        selectAllButton = findViewById(R.id.selectAllButton);

        selectAllButton.setOnClickListener(v -> {
            toggleCheckboxSelectionMode();
        });

        // Delete button
        ImageView deleteButton = findViewById(R.id.trashIcon);
        deleteButton.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete selected hikes")
                .setMessage("Are you sure you want to delete the selected hikes?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    ((HikeListViewAdapter) hikeListViewAdapter).deleteSelectedHikes(databaseHelper);
                    // Refresh search results after deletion
                    allHikes = databaseHelper.getAllHikes();
                    String currentQuery = searchView.getQuery().toString();
                    if (!currentQuery.isEmpty()) {
                        performSearch(currentQuery);
                    } else {
                        updateRecyclerView(allHikes);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
    }

    // Set up adapter listeners
    private void setupAdapterListeners() {
        // Selection mode listener
        ((HikeListViewAdapter) hikeListViewAdapter).setSelectionModeListener(isSelectionMode -> {
            optionsBar.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
        });

        // Edit listener
        ((HikeListViewAdapter) hikeListViewAdapter).setOnHikeClickListener((hike, position) -> {
            Intent intent = new Intent(this, HikeEditActivity.class);
            intent.putExtra("hikeIdToEdit", hike.getId());
            intent.putExtra("isEditMode", true);
            startActivity(intent);
        });

        // Checkbox selection change listener
        ((HikeListViewAdapter) hikeListViewAdapter).setHikeSelectionChangeListener(isAllHikesSelected -> {
            if (isAllHikesSelected) {
                selectAllButton.setText("Deselect All");
            } else {
                selectAllButton.setText("Select All");
            }
        });
    }

    // Toggle selection mode
    private void toggleCheckboxSelectionMode() {
        if (selectAllButton.getText().toString().equals("Select All")) {
            ((HikeListViewAdapter) hikeListViewAdapter).selectAllHikes();
            selectAllButton.setText("Deselect All");
        } else {
            ((HikeListViewAdapter) hikeListViewAdapter).deselectAllHikes();
            selectAllButton.setText("Select All");
        }
    }


    // Add test data
    // FOR TESTING ONLY
    private void addTestData(DatabaseHelper databaseHelper) {
        // Randomise data for variety
        String[] randomisedName = { "Vivian", "Mujina", "Sayu", "Jane", "Chitose", "Chiori"};
        String[] randomisedLocation = { "Mountain", "Forest", "River", "Desert", "Canyon"};
        String[] randomisedGear = { "Backpack", "Hiking Boots", "Water Bottle", "Map", "Compass", "First Aid Kit", "Tent" };
        String[] randomisedDescription = {
                "A beautiful hike with scenic views.",
                "A challenging trail through rugged terrain.",
                "A leisurely walk along a peaceful path.",
                "An adventurous route with exciting obstacles.",
                "A relaxing stroll in nature."
        };

        for (int i = 1; i < 10; i++) {
            int randomName1 = (int) (Math.random() * randomisedName.length);
            int randomName2;
            do {
                randomName2 = (int) (Math.random() * randomisedName.length);
            } while (randomName2 == randomName1);
            //
            int randomGear1 = (int) (Math.random() * randomisedGear.length);
            int randomGear2;
            do {
                randomGear2 = (int) (Math.random() * randomisedGear.length);
            } while (randomGear2 == randomGear1);
            int randomGear3;
            do {
                randomGear3 = (int) (Math.random() * randomisedGear.length);
            } while (randomGear3 == randomGear1 || randomGear3 == randomGear2);
            //
            int randomLocation = (int) (Math.random() * randomisedLocation.length);
            int randomDescription = (int) (Math.random() * randomisedDescription.length);
            databaseHelper.insertHikeDetails(
                    "Hike " + i,
                    randomisedLocation[randomLocation],
                    "2024-06-" + String.format("%02d", i),
                    (i % 2 == 0) ? "Yes" : "No",
                    (5 + i) + " km",
                    (i % 5) + 1,
                    new String[] { randomisedName[randomName1], randomisedName[randomName2] },
                    new String[] { randomisedGear[randomGear1], randomisedGear[randomGear2], randomisedGear[randomGear3] },
                    randomisedDescription[randomDescription]
            );
        }
    }

    // Set up the search view
    private void setupSearchView() {
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Show all hikes when search is empty
                    updateRecyclerView(allHikes);
                } else {
                    performSearch(newText);
                }
                return true;
            }
        });
    }

    // Check if an entry contains a keyword (case insensitive)
    private boolean containsIgnoreCase(String text, String keyword) {
        return text != null && text.toLowerCase().contains(keyword);
    }

    private boolean arrayContainsKeyword(String[] array, String keyword) {
        if (array == null) return false;
        for (String item : array) {
            if (containsIgnoreCase(item, keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean hikeContainsKeyword(Hike hike, String keyword) {
        // Search in all text fields
        return containsIgnoreCase(hike.getName(), keyword) ||
                containsIgnoreCase(hike.getLocation(), keyword) ||
                containsIgnoreCase(hike.getDate(), keyword) ||
                containsIgnoreCase(hike.getLength(), keyword) ||
                containsIgnoreCase(hike.getParkingAvailability(), keyword) ||
                containsIgnoreCase(String.valueOf(hike.getDifficulty()), keyword) ||
                containsIgnoreCase(hike.getDescription(), keyword) ||
                arrayContainsKeyword(hike.getHikeMembers(), keyword) ||
                arrayContainsKeyword(hike.getGears(), keyword);
    }

    // Perform search and update RecyclerView
    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            updateRecyclerView(allHikes);
            return;
        }

        String searchQuery = query.trim().toLowerCase();
        List<Hike> filteredHikes = new ArrayList<>();

        for (Hike hike : allHikes) {
            if (hikeContainsKeyword(hike, searchQuery)) {
                filteredHikes.add(hike);
            }
        }

        updateRecyclerView(filteredHikes);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Focus
        searchView.requestFocus();
        // Refresh data when returning from edit activity
        allHikes = databaseHelper.getAllHikes();
        String currentQuery = searchView.getQuery().toString();
        if (!currentQuery.isEmpty()) {
            performSearch(currentQuery);
        } else {
            updateRecyclerView(allHikes);
        }
    }
}