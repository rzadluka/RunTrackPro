package com.cs407.runtrackpro;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.Scanner;

public class DuringRunActivity extends AppCompatActivity{

    //code for timer control made referencing from https://stackoverflow.com/questions/4597690/how-to-set-timer-in-android
    TextView timer;
    TextView distanceCovered;
    TextView avgSpeed;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 12;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final DecimalFormat format = new DecimalFormat("0.00");
    long startTime = 0;
    int totalMinutes = 0;
    int totalSeconds = 0;

    double plan_distance =0;
    double distance = 0;
    double pace = 0;

    double speed;
    Location lastKnownLocation = null;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            totalSeconds = (int) (millis / 1000);
            totalMinutes = totalSeconds / 60;
            totalSeconds = totalSeconds % 60;

            timer.setText(String.format("%02d:%02d", totalMinutes, totalSeconds));

            //Calculate distance traveled since last update
            int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
            if(permission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }
            else {
                mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(DuringRunActivity.this, task -> {
                    Location currentLocation = task.getResult();
                    if(totalMinutes == 0 && totalSeconds == 0) {
                        lastKnownLocation = task.getResult();
                        distanceCovered.setText("0.00 mi");
                        avgSpeed.setText("0.00 mph");
                    }
                    else if(totalSeconds % 15 == 0 && task.isSuccessful() && currentLocation != null && lastKnownLocation != null) {
                        double distanceTraveled = lastKnownLocation.distanceTo(currentLocation);
                        //meters to miles
                        distanceTraveled = distanceTraveled/1609.34;
                        distance += distanceTraveled;
                        distanceCovered.setText("" + format.format(distance) + " mi");
                        //double speed = distance / ((totalMinutes * 60 + totalSeconds) / 3600.0);
                        speed = distance / ((totalMinutes * 60 + totalSeconds) / 3600.0);
                        avgSpeed.setText("" + format.format(speed) + " mph");
                        lastKnownLocation = currentLocation;
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

        timer = (TextView) findViewById(R.id.timer);
        distanceCovered = (TextView) findViewById(R.id.distance);
        avgSpeed = (TextView) findViewById(R.id.speed);
        Button endRunButton  = findViewById(R.id.endRun);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Button controlTimer = findViewById(R.id.timerControl);
        controlTimer.setText("Start");

        controlTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button controlTimer = (Button) v;
                if(controlTimer.getText().equals("Start")) {
                    startTime = System.currentTimeMillis() - (long)((totalMinutes * 60 + totalSeconds) * 1000);
                    timerHandler.postDelayed(timerRunnable, 0);
                    controlTimer.setText("Pause");
                }
                else {
                    timerHandler.removeCallbacks(timerRunnable);
                    controlTimer.setText("Start");
                }
            }
        });

        endRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToEndRun();
            }
        });

        //Change into map view
        Button MapButton =findViewById(R.id.mapButton);
        Intent intent =getIntent();
        String startLoc =intent.getStringExtra("start");
        String endLoc =intent.getStringExtra("end");

        //identify which plan, if the plan is destination, change km to miles.
        //-----------------------------------------------------------------------
        String RAW_distance =intent.getStringExtra("distance");
        String plan =intent.getStringExtra("plan");
        if(plan.equals("d")){
            String distance_in_km =RAW_distance;
            Scanner sc =new Scanner(distance_in_km);
            double raw_double_distance_in_km =sc.nextDouble();
            plan_distance =raw_double_distance_in_km*0.621371;
        }
        if(plan.equals("m")){
            double double_distance =Double.parseDouble(RAW_distance);
            plan_distance =double_distance;
        }
        else{
            //make a toast.
        }
        //debug
        String s=String.valueOf(plan_distance);
        Log.i(TAG,s);
        //-----------------------------------------------------------------------
        MapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(DuringRunActivity.this,MapTrackActivity.class);
                intent.putExtra("start",startLoc);
                intent.putExtra("end",endLoc);
                startActivity(intent);
            }
        });
    }


    public void moveToEndRun() {
        Intent intent = new Intent(this, RunCompleteActivity.class);
        intent.putExtra("time", String.format("%02d:%02d", totalMinutes, totalSeconds));
        intent.putExtra("distance", "" + format.format(distance));
        intent.putExtra("pace", "" + format.format(pace));
        intent.putExtra("avgSpeed",""+speed);
        startActivity(intent);
    }



}