package com.cs407.runtrackpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RunCompleteActivity extends AppCompatActivity {

    Button homeButton;
    TextView timeText;
    TextView distanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_complete);

        timeText = findViewById(R.id.time);
        distanceText = findViewById(R.id.distance);
        timeText.append(getIntent().getExtras().getString("time"));
        distanceText.append(getIntent().getExtras().getString("distance"));

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