package com.cs407.runtrackpro;

public class Stats {
    private final String date;
    private final String time;
    private final String distance;

    public Stats(String date, String time, String distance) {
        this.date = date;
        this.time = time;
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDistance() {
        return distance;
    }

}
