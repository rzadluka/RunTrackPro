package com.cs407.runtrackpro;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.maps.model.LatLng;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class DuringRunActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 12;
    private static final DecimalFormat format = new DecimalFormat("0.00");
    // code for timer control made referencing from https://stackoverflow.com/questions/4597690/how-to-set-timer-in-android
    TextView timer;
    TextView distanceCovered;
    TextView distanceLeft;
    TextView avgSpeed;
    TextView pace;
    long startTime = 0;
    int totalHours = 0;
    int totalMinutes = 0;
    int totalSeconds = 0;
    double plan_distance = 0;
    double distance = 0;
    Location lastKnownLocation = null;
    private ArrayList<String> userPath;
    Handler timerHandler = new Handler();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            totalSeconds = (int) (millis / 1000);
            totalHours = totalSeconds / 3600;
            totalMinutes = (totalSeconds % 3600) / 60;
            totalSeconds = totalSeconds % 60;

            timer.setText(String.format("%02d:%02d:%02d", totalHours, totalMinutes, totalSeconds));

            //Calculate distance traveled since last update
            int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(DuringRunActivity.this, task -> {
                    Location currentLocation = task.getResult();

                    if (totalMinutes == 0 && totalSeconds == 0) {
                        lastKnownLocation = task.getResult();
                        distanceCovered.setText("0.00 mi");
                        avgSpeed.setText("0.00 mph");
                        pace.setText("--:-- /mi");
                    } else if (totalSeconds % 5 == 0 && task.isSuccessful() && currentLocation != null && lastKnownLocation != null) {
                        // distance
                        // add point to user path
                        userPath.add(currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                        double distanceTraveled = lastKnownLocation.distanceTo(currentLocation);
                        distanceTraveled = distanceTraveled / 1609.34; //meters to miles
                        distance += distanceTraveled;
                        distanceCovered.setText(format.format(distance) + " mi");

                        // distance left
                        distanceLeft.setText(plan_distance - distance > 0 ? String.format("%.2f mi left", plan_distance - distance) : "0.00 mi left");

                        // speed
                        double speed = distance / ((totalMinutes * 60 + totalSeconds) / 3600.0);
                        avgSpeed.setText(format.format(speed) + " mph");
                        lastKnownLocation = currentLocation;

                        // pace
                        double totalTimeSeconds = (totalHours * 3600) + (totalMinutes * 60) + totalSeconds;
                        double pacePerMile = totalTimeSeconds / distance;
                        int minutesPerMile = (int) ((pacePerMile % 3600) / 60);
                        int secondsPerMile = (int) (pacePerMile % 60);
                        String formattedPace = minutesPerMile + secondsPerMile > 0 ? String.format("%02d:%02d /mi", minutesPerMile, secondsPerMile) : "--:-- /mi";
                        pace.setText(formattedPace);
                    }
                });
            }

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_run);

        timer = findViewById(R.id.timer);
        distanceCovered = findViewById(R.id.distance);
        distanceLeft = findViewById(R.id.distance_left);
        avgSpeed = findViewById(R.id.speed);
        pace = findViewById(R.id.pace);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        userPath = new ArrayList<>();

        ImageView controlTimer = findViewById(R.id.timerControl);
        controlTimer.setTag("Start");

        controlTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controlTimer.getTag().toString().equals("Start")) {
                    startTime = System.currentTimeMillis() - (long) ((totalMinutes * 60L + totalSeconds) * 1000);
                    timerHandler.postDelayed(timerRunnable, 0);
                    controlTimer.setTag("Pause");
                    controlTimer.setImageDrawable(getDrawable(R.drawable.ic_pause));
                    controlTimer.getDrawable().setTint(getColor(R.color.yellow));
                } else {
                    timerHandler.removeCallbacks(timerRunnable);
                    controlTimer.setTag("Start");
                    controlTimer.setImageDrawable(getDrawable(R.drawable.ic_start));
                    controlTimer.getDrawable().setTint(getColor(R.color.green));
                }
            }
        });

        ImageView endRunButton = findViewById(R.id.endRun);
        endRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToEndRun();
            }
        });

        //Change into map view
        ImageView MapButton = findViewById(R.id.mapButton);
        Intent intent = getIntent();
        String startLoc = intent.getStringExtra("start");
        String endLoc = intent.getStringExtra("end");

        //identify which plan, if the plan is destination, change km to miles.
        //-----------------------------------------------------------------------
        String RAW_distance = intent.getStringExtra("distance");
        String plan = intent.getStringExtra("plan");
        if (plan.equals("d")) {
            String distance_in_km = RAW_distance;
            Scanner sc = new Scanner(distance_in_km);
            double raw_double_distance_in_km = sc.nextDouble();
            plan_distance = raw_double_distance_in_km * 0.621371;
        }
        if (plan.equals("m")) {
            double double_distance = Double.parseDouble(RAW_distance);
            plan_distance = double_distance;
            distanceLeft.setText(String.format("%.2f mi left", plan_distance));
        }

        MapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DuringRunActivity.this, MapTrackActivity.class);
                intent.putExtra("start", startLoc);
                intent.putExtra("end", endLoc);
                intent.putExtra("plan", plan);
                intent.putExtra("path", userPath);
                startActivity(intent);
            }
        });
    }

    public void moveToEndRun() {
        // save data and run information
        SQLiteDatabase sqLiteDatabase = getApplicationContext().openOrCreateDatabase("stats",
                Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        String date = dateFormat.format(new Date());
        dbHelper.saveStats(date, String.format("%02d:%02d:%02d", totalHours, totalMinutes, totalSeconds),
                format.format(distance), formatPath(userPath));

        // move to end run activity
        Intent intent = new Intent(this, RunCompleteActivity.class);
        intent.putExtra("time", String.format("%02d:%02d:%02d", totalHours, totalMinutes, totalSeconds));
        intent.putExtra("distance", format.format(distance));
        intent.putExtra("speed", avgSpeed.getText().toString());
        intent.putExtra("pace", pace.getText().toString());
        intent.putExtra("path", userPath);
        startActivity(intent);
    }

    private String formatPath(ArrayList<String> path) {
        String pathStr = "";
        for (String point : path) {
            pathStr += point + ";";
        }
        return pathStr.substring(0, pathStr.length());
    }

}
