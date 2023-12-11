package com.cs407.runtrackpro;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class StatsDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int PATH_WIDTH = 15;
    TextView statsDetails;
    TextView distance;
    TextView time;
    TextView pace;
    String date;
    ArrayList<String> userPath;

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
            distance.setText(stats.getFormattedDistance());
            time.setText(stats.getFormattedTime());
            pace.setText(stats.getPace());
            userPath = stats.getPath();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.framgent_map);
            mapFragment.getMapAsync(this);
        }

        LinearLayout back = findViewById(R.id.goBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout delete = findViewById(R.id.delete);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawUserPath(userPath);

        zoomCamera(mMap);
    }

    private void zoomCamera(GoogleMap mMap) {
        ArrayList<LatLng> pathLatLng = convertStringToLatLng(userPath);

        // Move and animate camera to include the polyline in the view
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : pathLatLng) {
            builder.include(new com.google.android.gms.maps.model.LatLng(point.lat, point.lng));
        }

        LatLngBounds bounds = builder.build();
        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
    }

    private void drawUserPath(List<String> path) {
        // convert String ArrayList to LatLng
        ArrayList<LatLng> pathLatLng = convertStringToLatLng(path);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                List<com.google.android.gms.maps.model.LatLng> gmsPath = new ArrayList<>();
                for (com.google.maps.model.LatLng latLng : pathLatLng) {
                    gmsPath.add(new com.google.android.gms.maps.model.LatLng(latLng.lat, latLng.lng));
                }
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(gmsPath)
                        .width(PATH_WIDTH)
                        .color(R.color.secondary);
                mMap.addPolyline(polylineOptions);
            }
        });
    }

    private ArrayList<LatLng> convertStringToLatLng(List<String> path) {
        ArrayList<LatLng> pathLatLng = new ArrayList<>();
        for (String point : path) {
            String[] pointLatLng = point.split(",");
            pathLatLng.add(new LatLng(Double.parseDouble(pointLatLng[0]), Double.parseDouble(pointLatLng[1])));
        }
        return pathLatLng;
    }
}