package com.cs407.runtrackpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DuringRunActivity extends AppCompatActivity {

    //code for timer control made referencing from https://stackoverflow.com/questions/4597690/how-to-set-timer-in-android
    TextView timer;
    long startTime = 0;
    int totalMinutes = 0;
    int totalSeconds = 0;
    double distance = 0;
    double pace = 0;
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
        Button endRunButton  = findViewById(R.id.endRun);

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

    }

    public void moveToEndRun() {
        Intent intent = new Intent(this, RunCompleteActivity.class);
        intent.putExtra("time", String.format("%02d:%02d", totalMinutes, totalSeconds));
        intent.putExtra("distance", "" + distance);
        intent.putExtra("pace", "" + pace);
        startActivity(intent);
    }

}