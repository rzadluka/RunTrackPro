package com.cs407.runtrackpro;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanByDestinationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 12;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public PlanByDestinationFragment() {}

    public static PlanByDestinationFragment newInstance(String param1, String param2) {
        PlanByDestinationFragment fragment = new PlanByDestinationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public  PlacesClient placesClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_plan_by_destination, container, false);

        String API_KEY ="AIzaSyBwGEk3QqFSCRWgm063zpbmFhEWzEx-I7Q";
        Context appContext =getActivity().getApplicationContext();
        Places.initialize(appContext, API_KEY);
        placesClient =Places.createClient(appContext);
        GeoApiContext context =new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        View view =inflater.inflate(R.layout.fragment_plan_by_destination,container,false);

        EditText startText =(EditText) view.findViewById(R.id.destinationStartInput);
        startText.setFocusable(false);
        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList =Arrays.asList(Place.Field.ADDRESS
                        ,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent =new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(appContext);
                startAutocomplete.launch(intent);
            }
        });

        EditText endText =(EditText) view.findViewById(R.id.destinationEndInput);
        endText.setFocusable(false);
        endText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList =Arrays.asList(Place.Field.ADDRESS
                        ,Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent =new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(appContext);
                endAutocomplete.launch(intent);
            }
        });

        //Route generate button and function.
        Button generateRoute =(Button) view.findViewById(R.id.RouteButton);
        generateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startLoc =startText.getText().toString();
                String endLoc =endText.getText().toString();
                if(startLoc !=null && endLoc !=null){
                    showResult(startLoc,endLoc,context);
                }else{
                    //make a toast.
                }
            }
        });

        Button startRun =(Button) view.findViewById(R.id.StartRun_Destination);
        startRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startLoc =startText.getText().toString();
                String endLoc =endText.getText().toString();
                if(startLoc !=null && endLoc !=null){
                    Intent intent =new Intent(getActivity(),DuringRunActivity.class);
                    intent.putExtra("start",startLoc);
                    intent.putExtra("end",endLoc);
                    startActivity(intent);
                }else{
                    //make a toast.
                }
            }
        });
        return view;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.framgent_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
        });
    }

    private final ActivityResultLauncher<Intent> startAutocomplete =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result ->{
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        EditText startText =getView().findViewById(R.id.destinationStartInput);
                        startText.setText(place.getAddress());
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });

    private final ActivityResultLauncher<Intent> endAutocomplete =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result ->{
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        EditText startText =getView().findViewById(R.id.destinationEndInput);
                        startText.setText(place.getAddress());
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });

    //Show result, put marker and routes.
    private void showResult(String start, String end, GeoApiContext context){
        DirectionsApiRequest directions = DirectionsApi.newRequest(context)
                .origin(start)
                .destination(end)
                .mode(TravelMode.WALKING);

        directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                if(result !=null && result.routes.length >0){
                    LatLng startLatLng = new LatLng(result.routes[0].legs[0].startLocation.lat,
                            result.routes[0].legs[0].startLocation.lng);
                    LatLng endLatLng = new LatLng(result.routes[0].legs[0].endLocation.lat,
                            result.routes[0].legs[0].endLocation.lng);
                    //
                    adjustCamera(startLatLng,endLatLng);
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
    //

    //Zoom the camera to adjust the view (small bug)
    private void adjustCamera(LatLng startLoc, LatLng endLoc){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(mMap !=null){
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

    private void drawRoute(List<LatLng> path){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                List<com.google.android.gms.maps.model.LatLng> gmsPath =new ArrayList<>();
                for(com.google.maps.model.LatLng latLng : path){
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

    /**
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.framgent_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            displayMyLocation();
        });
    }

    private void displayMyLocation() {
        int permission = ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this.getActivity(), task -> {
                Location mLastKnownLocation = task.getResult();
                mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())));
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayMyLocation();
            }
        }
    }**/
}