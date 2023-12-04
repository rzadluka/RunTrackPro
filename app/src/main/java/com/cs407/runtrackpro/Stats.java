package com.cs407.runtrackpro;

public class Stats {
    private final String date;
    private final String time;
    private final String distance;
    private final String speed;

    public Stats(String date, String time, String distance, String speed) {
        this.date = date;
        this.time = time;
        this.distance = distance;
        this.speed = speed;
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

    public String getSpeed() {
        return speed;
    }
}
