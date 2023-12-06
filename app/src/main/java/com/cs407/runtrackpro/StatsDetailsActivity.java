package com.cs407.runtrackpro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StatsDetailsActivity extends AppCompatActivity {

    TextView statsDetails;
    TextView distance;
    TextView time;
    TextView pace;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_details);

        statsDetails = findViewById(R.id.statsDetails);
        distance = findViewById(R.id.distance);
        time = findViewById(R.id.time);
        pace = findViewById(R.id.pace);
        int statId = getIntent().getIntExtra("statId", -1);

        if (statId != -1) {
            Stats stats = HomeFragment.stats1.get(statId);
            statsDetails.setText("Your run on " + stats.getDate());
            date = stats.getDate();
            distance.setText(formatDistance(Double.parseDouble(stats.getDistance())));
            time.setText(formatTime(getTimeInSeconds(stats)));
            pace.setText(formatPace(getPace(stats)));
        }

        Button back = findViewById(R.id.goBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statId == -1) {
                    Toast.makeText(StatsDetailsActivity.this, "Cannot delete stat that doesn't exist", Toast.LENGTH_SHORT).show();
                } else {
                    SQLiteDatabase sqLiteDatabase = getApplicationContext().openOrCreateDatabase("stats",
                            Context.MODE_PRIVATE, null);
                    DBHelper dbHelper = new DBHelper(sqLiteDatabase);
                    dbHelper.deleteStats(date);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
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

    private double getPace(Stats stats) {
        int totalTimeSeconds = 0;
        double distance = Double.parseDouble(stats.getDistance());

        // Split the time string into minutes and seconds
        String[] timeParts = stats.getTime().split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);

        // Convert time to seconds and add to totalTimeSeconds
        totalTimeSeconds += hours * 3600 + minutes * 60 + seconds;

        return distance > 0 ? (totalTimeSeconds / distance) : 0;
    }

    private int getTimeInSeconds(Stats stats) {
        String[] timeParts = stats.getTime().split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }
}