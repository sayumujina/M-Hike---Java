package com.example.hikerdatabase.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

import com.example.hikerdatabase.HikesInfo.Hike;
import com.example.hikerdatabase.ObservationsInfo.Observation;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "hikes_database";
    public static final int DATABASE_VERSION = 1;

    // Hike table
    public static final String HIKE_TABLE_NAME = "hikes";
    public static final String HIKE_ID_COLUMN = "id";
    public static final String HIKE_NAME_COLUMN = "name";
    public static final String HIKE_LOCATION_COLUMN = "location";
    public static final String HIKE_DATE_COLUMN = "date";
    public static final String HIKE_PARKING_COLUMN = "parking";
    public static final String HIKE_LENGTH_COLUMN = "length";
    public static final String HIKE_DIFFICULTY_COLUMN = "difficulty";
    public static final String HIKE_DESCRIPTION_COLUMN = "description";
    public static final String HIKE_MEMBERS_COLUMN = "members";
    public static final String HIKE_GEAR_COLUMN = "gear";

    // Observation table constants
    public static final String OBSERVATIONS_TABLE_NAME = "observations";
    public static final String OBSERVATIONS_ID_COLUMN = "id";
    public static final String OBSERVATIONS_HIKE_ID_COLUMN = "hikeId";
    public static final String OBSERVATIONS_NAME_COLUMN = "name";
    public static final String OBSERVATIONS_COMMENT_COLUMN = "comment";
    public static final String OBSERVATIONS_DATE_COLUMN = "date";
    public static final String OBSERVATIONS_TIME_COLUMN = "time";


    public static final String CREATE_HIKES_TABLE = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s INT NOT NULL, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT);",
            HIKE_TABLE_NAME,
            HIKE_ID_COLUMN,
            HIKE_NAME_COLUMN,
            HIKE_LOCATION_COLUMN,
            HIKE_DATE_COLUMN,
            HIKE_PARKING_COLUMN,
            HIKE_LENGTH_COLUMN,
            HIKE_DIFFICULTY_COLUMN,
            HIKE_MEMBERS_COLUMN,
            HIKE_GEAR_COLUMN,
            HIKE_DESCRIPTION_COLUMN);

    public static final String CREATE_OBSERVATIONS_TABLE = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s INT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);",
            OBSERVATIONS_TABLE_NAME,
            OBSERVATIONS_ID_COLUMN,
            OBSERVATIONS_HIKE_ID_COLUMN,
            OBSERVATIONS_NAME_COLUMN,
            OBSERVATIONS_COMMENT_COLUMN,
            OBSERVATIONS_DATE_COLUMN,
            OBSERVATIONS_TIME_COLUMN,
            OBSERVATIONS_HIKE_ID_COLUMN, // Foreign key
            HIKE_TABLE_NAME, // Referenced table
            HIKE_ID_COLUMN); // Referenced column from hikes table
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_HIKES_TABLE);
            db.execSQL(CREATE_OBSERVATIONS_TABLE);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error creating database: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + HIKE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + OBSERVATIONS_TABLE_NAME);
            Log.w(this.getClass().getName(), DATABASE_NAME + " table upgraded from version " + oldVersion + " to " + newVersion);
            onCreate(db);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error upgrading database: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    // Insert hike details into the database
    public void insertHikeDetails(String name, String location, String date,
                                  String isParkingAvailable, double length,
                                  int difficulty, String[] hikeMembers, String[] gear, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(HIKE_NAME_COLUMN, name);
        contentValues.put(HIKE_LOCATION_COLUMN, location);
        contentValues.put(HIKE_DATE_COLUMN, date);
        contentValues.put(HIKE_PARKING_COLUMN, isParkingAvailable);
        contentValues.put(HIKE_LENGTH_COLUMN, length);
        contentValues.put(HIKE_DIFFICULTY_COLUMN, difficulty);
        contentValues.put(HIKE_MEMBERS_COLUMN, String.join(",", hikeMembers));
        contentValues.put(HIKE_GEAR_COLUMN, String.join(",", gear));
        contentValues.put(HIKE_DESCRIPTION_COLUMN, description);
        db.insert(HIKE_TABLE_NAME, null, contentValues);
    }

    // Update hike details in the database
    public boolean updateHikeDetails(int id, String name, String location, String date,
                                  String isParkingAvailable, double length,
                                  int difficulty, String[] hikeMembers, String[] gear, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(HIKE_NAME_COLUMN, name);
        contentValues.put(HIKE_LOCATION_COLUMN, location);
        contentValues.put(HIKE_DATE_COLUMN, date);
        contentValues.put(HIKE_PARKING_COLUMN, isParkingAvailable);
        contentValues.put(HIKE_LENGTH_COLUMN, length);
        contentValues.put(HIKE_DIFFICULTY_COLUMN, difficulty);
        contentValues.put(HIKE_MEMBERS_COLUMN, String.join(",", hikeMembers));
        contentValues.put(HIKE_GEAR_COLUMN, String.join(",", gear));
        contentValues.put(HIKE_DESCRIPTION_COLUMN, description);
        int rowsAffected = db.update(HIKE_TABLE_NAME, contentValues, HIKE_ID_COLUMN + " = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

    // Retrieve a hike from the database by ID
    public Hike getHikeById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor result = db.query(
                HIKE_TABLE_NAME,
                new String[]{HIKE_ID_COLUMN,
                        HIKE_NAME_COLUMN,
                        HIKE_LOCATION_COLUMN,
                        HIKE_DATE_COLUMN,
                        HIKE_PARKING_COLUMN,
                        HIKE_LENGTH_COLUMN,
                        HIKE_DIFFICULTY_COLUMN,
                        HIKE_MEMBERS_COLUMN,
                        HIKE_GEAR_COLUMN,
                        HIKE_DESCRIPTION_COLUMN},
                HIKE_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        // Convert the result to a Hike object
        if (result.moveToFirst()) {
            Hike hike = new Hike(
                    result.getInt(result.getColumnIndexOrThrow(HIKE_ID_COLUMN)),
                    result.getString(result.getColumnIndexOrThrow(HIKE_NAME_COLUMN)),
                    result.getString(result.getColumnIndexOrThrow(HIKE_LOCATION_COLUMN)),
                    result.getString(result.getColumnIndexOrThrow(HIKE_DATE_COLUMN)),
                    result.getString(result.getColumnIndexOrThrow(HIKE_PARKING_COLUMN)),
                    result.getDouble(result.getColumnIndexOrThrow(HIKE_LENGTH_COLUMN)),
                    result.getInt(result.getColumnIndexOrThrow(HIKE_DIFFICULTY_COLUMN)),
                    result.getString(result.getColumnIndexOrThrow(HIKE_MEMBERS_COLUMN)).split(","),
                    result.getString(result.getColumnIndexOrThrow(HIKE_GEAR_COLUMN)).split(","),
                    result.getString(result.getColumnIndexOrThrow(HIKE_DESCRIPTION_COLUMN))
            );
            result.close();
            return hike;
        } else {
            return null;
        }
    }

    // Delete a hike from the database by ID
    public void deleteHikeById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(HIKE_TABLE_NAME, HIKE_ID_COLUMN + " = ?", new String[]{String.valueOf(id)});
    }

    // Retrieve all hike details from the database\
    public ArrayList<Hike> getAllHikes() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor results = db.query(
                HIKE_TABLE_NAME,
                new String[]{HIKE_ID_COLUMN,
                        HIKE_NAME_COLUMN,
                        HIKE_LOCATION_COLUMN,
                        HIKE_DATE_COLUMN,
                        HIKE_PARKING_COLUMN,
                        HIKE_LENGTH_COLUMN,
                        HIKE_DIFFICULTY_COLUMN,
                        HIKE_MEMBERS_COLUMN,
                        HIKE_GEAR_COLUMN,
                        HIKE_DESCRIPTION_COLUMN},
                null, null, null, null,
                HIKE_ID_COLUMN + " DESC"
        );

        // Convert the results to an array of Hike objects
        Hike[] hikes = new Hike[results.getCount()];
        int hikeIndex = 0;
        while (results.moveToNext()) {
            hikes[hikeIndex] = new Hike(
                    results.getInt(results.getColumnIndexOrThrow(HIKE_ID_COLUMN)),
                    results.getString(results.getColumnIndexOrThrow(HIKE_NAME_COLUMN)),
                    results.getString(results.getColumnIndexOrThrow(HIKE_LOCATION_COLUMN)),
                    results.getString(results.getColumnIndexOrThrow(HIKE_DATE_COLUMN)),
                    results.getString(results.getColumnIndexOrThrow(HIKE_PARKING_COLUMN)),
                    results.getDouble(results.getColumnIndexOrThrow(HIKE_LENGTH_COLUMN)),
                    results.getInt(results.getColumnIndexOrThrow(HIKE_DIFFICULTY_COLUMN)),
                    results.getString(results.getColumnIndexOrThrow(HIKE_MEMBERS_COLUMN)).split(","),
                    results.getString(results.getColumnIndexOrThrow(HIKE_GEAR_COLUMN)).split(","),
                    results.getString(results.getColumnIndexOrThrow(HIKE_DESCRIPTION_COLUMN))
            );
            hikeIndex++;
        }
        results.close();
        return new ArrayList<>(java.util.Arrays.asList(hikes));
    }

    // Retrieve the latest hike ID
    public int getLatestHikeId() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + HIKE_ID_COLUMN + ") AS max_id FROM " + HIKE_TABLE_NAME, null);
        int latestId = -1;
        if (cursor.moveToFirst()) {
            latestId = cursor.getInt(cursor.getColumnIndexOrThrow("max_id"));
        }
        cursor.close();
        return latestId;
    }

    // Insert an observation for a hike
    public void insertObservation(int hikeId, String name, String date, String time, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");

        ContentValues contentValues = new ContentValues();
        contentValues.put(OBSERVATIONS_HIKE_ID_COLUMN, hikeId);
        contentValues.put(OBSERVATIONS_NAME_COLUMN, name);
        contentValues.put(OBSERVATIONS_DATE_COLUMN, date);
        contentValues.put(OBSERVATIONS_TIME_COLUMN, time);
        contentValues.put(OBSERVATIONS_COMMENT_COLUMN, comment);

        db.insert(OBSERVATIONS_TABLE_NAME, null, contentValues);
        Log.d("ObsDatabaseHelper", "Inserted observation for hike ID: " + hikeId);
    }

    // Update an observation by its ID
    public boolean updateObservation(int observationId, int hikeId, String name, String date, String time, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(OBSERVATIONS_HIKE_ID_COLUMN, hikeId);
        contentValues.put(OBSERVATIONS_NAME_COLUMN, name);
        contentValues.put(OBSERVATIONS_DATE_COLUMN, date);
        contentValues.put(OBSERVATIONS_TIME_COLUMN, time);
        contentValues.put(OBSERVATIONS_COMMENT_COLUMN, comment);
        int rowsAffected = db.update(OBSERVATIONS_TABLE_NAME, contentValues,
                OBSERVATIONS_ID_COLUMN + " = ?", new String[]{String.valueOf(observationId)});
        return rowsAffected > 0;
    }

    // Retrieve all observations for a specific hike
    public ArrayList<Observation> getObservationsFromHike(int hikeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Observation> observations = new ArrayList<>();
        Cursor cursor = db.query(OBSERVATIONS_TABLE_NAME, null, OBSERVATIONS_HIKE_ID_COLUMN + " = ?",
                new String[]{String.valueOf(hikeId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(OBSERVATIONS_ID_COLUMN));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS_NAME_COLUMN));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS_DATE_COLUMN));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS_TIME_COLUMN));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS_COMMENT_COLUMN));

                Observation observation = new Observation(id, hikeId, name, date, time, comment);
                observations.add(observation);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return observations;
    }

    // Delete observation by its ID
    public void deleteObservationById(int observationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OBSERVATIONS_TABLE_NAME, OBSERVATIONS_ID_COLUMN + " = ?", new String[]{String.valueOf(observationId)});
    }

    // Delete all observations for a specific hike
    public void deleteObservationsByHikeId(int hikeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OBSERVATIONS_TABLE_NAME, OBSERVATIONS_HIKE_ID_COLUMN + " = ?", new String[]{String.valueOf(hikeId)});
    }

}


