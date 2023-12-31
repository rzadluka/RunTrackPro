package com.cs407.runtrackpro;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 12;
    private static final int ZOOM_LEVEL = 18;
    private static final int PATH_WIDTH = 20;
    private ArrayList<String> userPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_track);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Rebuild the map under same condition.
        getInitLocation();

        String API_KEY = "AIzaSyBwGEk3QqFSCRWgm063zpbmFhEWzEx-I7Q";
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(mMap != null){
                    updateLocationInfo(location);
                }
            }
        };

        if(Build.VERSION.SDK_INT < 23){
            startListening();
        }else{
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
            } else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null && mMap != null){
                    updateLocationInfo(location);
                }
            }
        }

        // get userPath
        userPath = getIntent().getExtras().getStringArrayList("path");

        LinearLayout BackButton = findViewById(R.id.BackButton);
        Intent intent = getIntent();
        String startLoc = intent.getStringExtra("start");
        String endLoc = intent.getStringExtra("end");
        String plan = intent.getStringExtra("plan");
        if(plan.equals("d")){
            ReBuildPath(startLoc, endLoc, context);
        }

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void startListening(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
    public void getInitLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        Task<Location> task = mFusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.framgent_map);
                    mapFragment.getMapAsync(googleMap -> {
                        mMap = googleMap;
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        com.google.android.gms.maps.model.LatLng gms_latLng = new com.google.android.gms.maps.model.LatLng(latitude,longitude);
                        // Create a new marker at the updated location
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(gms_latLng)
                                .title("Current Location")
                                .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_marker));
                        previousMarker = mMap.addMarker(markerOptions);

                        // Move the camera to the new location
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gms_latLng, ZOOM_LEVEL));
                    });
                }
            }
        });
    }


    public void updateLocationInfo(Location location){
        // Remove previous marker if it exists
        if (previousMarker != null) {
            previousMarker.remove();
        }
         double latitude = location.getLatitude();
         double longitude = location.getLongitude();
         com.google.android.gms.maps.model.LatLng gms_latLng = new com.google.android.gms.maps.model.LatLng(latitude, longitude);
         // Create a new marker at the updated location
         MarkerOptions markerOptions = new MarkerOptions()
                 .position(gms_latLng)
                 .title("CurrentLocation")
                 .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_map_marker));
         previousMarker = mMap.addMarker(markerOptions);

         // update and draw user's path
         userPath.add(latitude + "," + longitude);
         drawUserPath(userPath);

         // Move the camera to the new location
         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gms_latLng, ZOOM_LEVEL));
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

                    addPin(startLatLng, endLatLng);
                    drawRoute(result.routes[0].overviewPolyline.decodePath());
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

                    mMap.addMarker(new MarkerOptions()
                            .position(gmsStartLocation)
                            .title("Start Location")
                            .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_start_map)));
                    mMap.addMarker(new MarkerOptions()
                            .position(gmsEndLocation)
                            .title("End Location")
                            .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_finish)));
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
                        .width(PATH_WIDTH)
                        .color(R.color.primary);
                mMap.addPolyline(polylineOptions);
            }
        });
    }

    private void drawUserPath(List<String> path) {
        // convert String ArrayList to LatLng
        ArrayList<LatLng> pathLatLng = new ArrayList<>();
        for (String point : path) {
            String[] pointLatLng = point.split(",");
            pathLatLng.add(new LatLng(Double.parseDouble(pointLatLng[0]), Double.parseDouble(pointLatLng[1])));
        }

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
                        .color(R.color.black);
                mMap.addPolyline(polylineOptions);
            }
        });
    }

    // code from: https://www.geeksforgeeks.org/how-to-add-custom-marker-to-google-maps-in-android/
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);

        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}