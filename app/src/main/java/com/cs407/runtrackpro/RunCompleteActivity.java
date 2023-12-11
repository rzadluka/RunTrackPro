package com.cs407.runtrackpro;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class RunCompleteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int PATH_WIDTH = 15;
    LinearLayout homeButton;
    TextView timeText;
    TextView distanceText;
    TextView speedText;
    TextView paceText;
    ArrayList<String> userPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_complete);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.framgent_map);
        mapFragment.getMapAsync(this);

        userPath = getIntent().getExtras().getStringArrayList("path");

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawUserPath(userPath);

        ArrayList<LatLng> pathLatLng = convertStringToLatLng(userPath);

        // Move and animate camera to include the polyline in the view
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : pathLatLng) {
            builder.include(new com.google.android.gms.maps.model.LatLng(point.lat, point.lng));
        }

        // Use ViewTreeObserver to wait for the layout to be measured
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.framgent_map);
        if (mapFragment != null && mapFragment.getView() != null) {
            ViewTreeObserver viewTreeObserver = mapFragment.getView().getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mapFragment.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        LatLngBounds bounds = builder.build();
                        int padding = 100;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);
                    }
                });
            }
        }
    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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