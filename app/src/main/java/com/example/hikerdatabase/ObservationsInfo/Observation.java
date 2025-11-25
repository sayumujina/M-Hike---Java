package com.example.hikerdatabase.ObservationsInfo;

public class Observation {
    private int id;
    private int hikeId;
    private String name;
    private String date;
    private String time;
    private String comments;

    public Observation(int id, int hikeId, String name, String date, String time, String comments) {
        this.id = id;
        this.hikeId = hikeId;
        this.name = name;
        this.date = date;
        this.time = time;
        this.comments = comments;
    }

    public int getObservationId() {
        return id;
    }

    public String getObservationName() {
        return name;
    }

    public int getHikeId() {
        return hikeId;
    }

    public String getObservationDate() {
        return date;
    }
    public String getObservationTime() { return time; }

    public String getObservationComments() {
        return comments;
    }
}
