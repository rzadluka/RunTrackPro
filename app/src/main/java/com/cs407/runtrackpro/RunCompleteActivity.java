package com.cs407.runtrackpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RunCompleteActivity extends AppCompatActivity {

    LinearLayout homeButton;
    TextView timeText;
    TextView distanceText;
    TextView speedText;
    TextView paceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_complete);

        timeText = findViewById(R.id.time);
        distanceText = findViewById(R.id.distance);
        speedText = findViewById(R.id.speed);
        paceText = findViewById(R.id.avgPace);

        String time = getIntent().getExtras().getString("time");
        String distance = getIntent().getExtras().getString("distance");
        String speed = getIntent().getExtras().getString("speed");
        String pace = getIntent().getExtras().getString("pace");

        timeText.append(time);
        distanceText.append(distance + " mi");
        speedText.append(speed);
        paceText.append(pace);


        homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}