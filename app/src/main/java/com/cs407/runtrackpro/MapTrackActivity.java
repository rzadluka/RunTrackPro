package com.cs407.runtrackpro;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapTrackActivity extends AppCompatActivity {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

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
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        locationManager =(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener =new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);
            }
        };

        if(Build.VERSION.SDK_INT <23){
            startListening();
        }else{
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location !=null && mMap !=null){
                    updateLocationInfo(location);
                }
            }
        }

        Button BackButton = findViewById(R.id.BackButton);
        Intent intent = getIntent();
        String startLoc = intent.getStringExtra("start");
        String endLoc = intent.getStringExtra("end");
        String plan =intent.getStringExtra("plan");
        if(plan.equals("d")){
            ReBuildPath(startLoc, endLoc, context);
        }
        if(plan.equals("m")) {

        }
        else{
            //make a toast.
        }
        //

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void startListening(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationManager =(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requesCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requesCode, permissions,grantResults);

        if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }

    }

    Marker previousMarker;
    public void updateLocationInfo(Location location){
        // Remove previous marker if it exists
        if (previousMarker != null) {
            previousMarker.remove();
        }
         double latitude = location.getLatitude();
         double longitude = location.getLongitude();
         com.google.android.gms.maps.model.LatLng gms_latLng =new com.google.android.gms.maps.model.LatLng(latitude,longitude);
         // Create a new marker at the updated location
         MarkerOptions markerOptions = new MarkerOptions().position(gms_latLng).title("CurrentLocation");
         previousMarker = mMap.addMarker(markerOptions);

         // Move the camera to the new location
         mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gms_latLng, 16));

    }

    private void ReBuildPath(String start, String end, GeoApiContext context) {
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
                    addPin(startLatLng, endLatLng);
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
    private void addPin(LatLng startLoc, LatLng endLoc) {
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

                    //mMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(bounds, 100));
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