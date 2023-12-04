package com.cs407.runtrackpro;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

public class MapTrackActivity extends AppCompatActivity {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_track);

        //Rebuild the map under same condition.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.framgent_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
        });
        String API_KEY = "AIzaSyBwGEk3QqFSCRWgm063zpbmFhEWzEx-I7Q";
        Context appContext = this.getApplicationContext();
        Places.initialize(appContext, API_KEY);
        PlacesClient placesClient = Places.createClient(appContext);
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        Button BackButton = findViewById(R.id.BackButton);
        Intent intent = getIntent();
        String startLoc = intent.getStringExtra("start");
        String endLoc = intent.getStringExtra("end");
        ReBuildMap(startLoc, endLoc, context);
        //

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ReBuildMap(String start, String end, GeoApiContext context) {
        DirectionsApiRequest directions = DirectionsApi.newRequest(context)
                .origin(start)
                .destination(end)
                .mode(TravelMode.WALKING);
        directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                if (result != null && result.routes.length > 0) {
                    LatLng startLatLng = new LatLng(result.routes[0].legs[0].startLocation.lat,
                            result.routes[0].legs[0].startLocation.lng);
                    LatLng endLatLng = new LatLng(result.routes[0].legs[0].endLocation.lat,
                            result.routes[0].legs[0].endLocation.lng);
                    //
                    adjustCamera(startLatLng, endLatLng);
                    drawRoute(result.routes[0].overviewPolyline.decodePath());
                    //
                }
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "Directions API request failed", e);
            }
        });
    }

    //Zoom the camera to adjust the view (small bug)
    private void adjustCamera(LatLng startLoc, LatLng endLoc) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mMap != null) {
                    com.google.android.gms.maps.model.LatLng gmsStartLocation =
                            new com.google.android.gms.maps.model.LatLng(startLoc.lat, startLoc.lng);

                    com.google.android.gms.maps.model.LatLng gmsEndLocation =
                            new com.google.android.gms.maps.model.LatLng(endLoc.lat, endLoc.lng);

                    com.google.android.gms.maps.model.LatLngBounds.Builder builder =
                            new com.google.android.gms.maps.model.LatLngBounds.Builder();

                    builder.include(gmsStartLocation);
                    builder.include(gmsEndLocation);
                    com.google.android.gms.maps.model.LatLngBounds bounds = builder.build();

                    mMap.addMarker(new MarkerOptions().position(gmsStartLocation).title("Start Location"));
                    mMap.addMarker(new MarkerOptions().position(gmsEndLocation).title("End Location"));

                    mMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(bounds, 100));
                }
            }
        });
    }
    //

    private void drawRoute(List<LatLng> path) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                List<com.google.android.gms.maps.model.LatLng> gmsPath = new ArrayList<>();
                for (com.google.maps.model.LatLng latLng : path) {
                    gmsPath.add(new com.google.android.gms.maps.model.LatLng(latLng.lat, latLng.lng));
                }
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(gmsPath)
                        .width(10)
                        .color(Color.BLUE);
                mMap.addPolyline(polylineOptions);
            }
        });
    }
}