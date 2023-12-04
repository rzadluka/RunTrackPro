package com.cs407.runtrackpro;

import static android.content.ContentValues.TAG;
import static com.cs407.runtrackpro.DBHelper.sqLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        //Create a DBhelper.
        DBHelper dbHelper =DBHelper.getInstance();
        //Get date.
        DateFormat dateFormat = new SimpleDateFormat("MM/DD/YYYY HH:mm:ss");
        String date =dateFormat.format(new Date());
        //Get distance.
        String distance =getIntent().getStringExtra("distance");
        //Get time
        String time =getIntent().getStringExtra("time");
        //Get average speed
        String avgSpeed =getIntent().getStringExtra("avgSpeed");


        homeButton = findViewById(R.id.home_button);


        dbHelper.saveStats(date,time,distance,avgSpeed);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                Log.i(TAG,date);
                Log.i(TAG,distance);
                Log.i(TAG,time);
                Log.i(TAG,avgSpeed);**/
                goToHome();
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}