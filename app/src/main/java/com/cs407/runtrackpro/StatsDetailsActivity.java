package com.cs407.runtrackpro;

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
    TextView speed;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_details);

        statsDetails = findViewById(R.id.statsDetails);
        distance = findViewById(R.id.distance);
        time = findViewById(R.id.time);
        speed = findViewById(R.id.speed);
        int statId = getIntent().getIntExtra("statId", -1);

        if (statId != -1) {
            Stats stats = HomeFragment.stats1.get(statId);
            statsDetails.setText("Your run on " + stats.getDate());
            date = stats.getDate();
            distance.setText(stats.getDistance());
            time.setText(stats.getTime());
            speed.setText(stats.getSpeed());
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
}