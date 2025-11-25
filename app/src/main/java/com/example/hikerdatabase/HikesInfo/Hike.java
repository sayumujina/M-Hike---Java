package com.example.hikerdatabase.HikesInfo;

public class Hike {
    private final int id;
    private final String name;
    private final String location;
    private final String date;
    private final String isParkingAvailable;
    private final String length;
    private final int difficulty;
    private final String[] hikeMembers; // Optional
    private final String[] gear; // Optional
    private final String description; // Optional

    public Hike(int id, String name, String location, String date, String isParkingAvailable,
                String length, int difficulty, String[] hikeMembers, String[] gear, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.isParkingAvailable = isParkingAvailable;
        this.length = length;
        this.difficulty = difficulty;
        this.hikeMembers = hikeMembers;
        this.gear = gear;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getParkingAvailability() {
        return isParkingAvailable;
    }

    public String getLength() {
        return length;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String[] getHikeMembers() {
        return hikeMembers;
    }

    public String[] getGears() {
        return gear;
    }

    public String getDescription() {
        return description;
    }

}
