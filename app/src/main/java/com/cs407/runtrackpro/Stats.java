package com.cs407.runtrackpro;

import android.annotation.SuppressLint;

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

    public String getPace() {
        return formatPace(getRawPace());
    }

    public String getFormattedTime() {
        return formatTime(getTimeInSeconds(time));
    }

    public String getFormattedDistance() {
        return formatDistance(Double.parseDouble(distance));
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int totalTimeSeconds) {
        int hours = totalTimeSeconds / 3600;
        int minutes = (totalTimeSeconds % 3600) / 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    @SuppressLint("DefaultLocale")
    private String formatDistance(double totalDistance) {
        return String.format("%.2f", totalDistance) + " mi";
    }

    @SuppressLint("DefaultLocale")
    private String formatPace(double totalPace) {
        int hours = (int) (totalPace / 3600);
        int minutes = (int) ((totalPace % 3600) / 60);
        int seconds = (int) (totalPace % 60);
        if (hours >= 1) {
            return hours + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + " /mi";
        }
        return minutes + ":" + String.format("%02d", seconds) + " /mi";
    }

    private int getTimeInSeconds(String time) {
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }

    private double getRawPace() {
        int totalTimeSeconds = 0;
        double distance = Double.parseDouble(this.distance);

        // Split the time string into minutes and seconds
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);

        // Convert time to seconds and add to totalTimeSeconds
        totalTimeSeconds += hours * 3600 + minutes * 60 + seconds;

        return distance > 0 ? (totalTimeSeconds / distance) : 0;
    }

}
