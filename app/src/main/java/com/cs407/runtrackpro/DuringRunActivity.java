package com.cs407.runtrackpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DuringRunActivity extends AppCompatActivity {

    //code for timer control made referencing from https://stackoverflow.com/questions/4597690/how-to-set-timer-in-android
    TextView timer;
    long startTime = 0;
    int totalMinutes = 0;
    int totalSeconds = 0;
    Handler timerHandler = new Handler();

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            totalSeconds = (int) (millis / 1000);
            totalMinutes = totalSeconds / 60;
            totalSeconds = totalSeconds % 60;

            timer.setText(String.format("%02d:%02d", totalMinutes, totalSeconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_run);

        timer = (TextView) findViewById(R.id.timer);

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

        Button endRun = findViewById(R.id.endRun);
        endRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save data and run information
                String runTime = timer.getText().toString();

                // TODO placeholders for distance and speed
                String distance = "0.0";
                String speed = "0.0";

                SQLiteDatabase sqLiteDatabase = getApplicationContext().openOrCreateDatabase("stats",
                        Context.MODE_PRIVATE, null);
                DBHelper dbHelper = new DBHelper(sqLiteDatabase);

                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
                String date = dateFormat.format(new Date());
                dbHelper.saveStats(date, runTime, distance, speed);

                goToRunComplete(runTime);
            }
        });
    }

    private void goToRunComplete(String runTime) {
        Intent intent = new Intent(this, RunCompleteActivity.class);
        intent.putExtra("runTime", runTime);
        startActivity(intent);
    }
}