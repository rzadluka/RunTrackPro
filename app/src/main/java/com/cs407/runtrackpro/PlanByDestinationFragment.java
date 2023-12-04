package com.cs407.runtrackpro;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlanByDestinationFragment extends Fragment {
    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        EditText startText = getView().findViewById(R.id.destinationStartInput);
                        startText.setText(place.getAddress());
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });
    private final ActivityResultLauncher<Intent> endAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        EditText startText = getView().findViewById(R.id.destinationEndInput);
                        startText.setText(place.getAddress());
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });
    public PlacesClient placesClient;
    //
    String distance;
    private GoogleMap mMap;
    private ApiInterface apiInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_plan_by_destination, container, false);

        String API_KEY = "AIzaSyBwGEk3QqFSCRWgm063zpbmFhEWzEx-I7Q";
        Context appContext = getActivity().getApplicationContext();
        Places.initialize(appContext, API_KEY);
        placesClient = Places.createClient(appContext);
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        View view = inflater.inflate(R.layout.fragment_plan_by_destination, container, false);

        //
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
        //


        EditText startText = (EditText) view.findViewById(R.id.destinationStartInput);
        startText.setFocusable(false);
        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        , Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(appContext);
                startAutocomplete.launch(intent);
            }
        });

        EditText endText = (EditText) view.findViewById(R.id.destinationEndInput);
        endText.setFocusable(false);
        endText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        , Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(appContext);
                endAutocomplete.launch(intent);
            }
        });

        //Route generate button and function.
        Button generateRoute = (Button) view.findViewById(R.id.RouteButton);
        generateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startLoc = startText.getText().toString();
                String endLoc = endText.getText().toString();
                if (startLoc != null && endLoc != null) {
                    showResult(startLoc, endLoc, context);
                } else {
                    //make a toast.
                }
            }
        });

        Button startRun = (Button) view.findViewById(R.id.StartRun_Destination);
        startRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startLoc = startText.getText().toString();
                String endLoc = endText.getText().toString();
                if (startLoc != null && endLoc != null) {
                    DistanceCal(startLoc, endLoc);
                } else {
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

    //Show result, put marker and routes.
    private void showResult(String start, String end, GeoApiContext context) {
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

    private void DistanceCal(String start, String end) {
        apiInterface.getDistance("AIzaSyBwGEk3QqFSCRWgm063zpbmFhEWzEx-I7Q", start, end)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Result>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //make a toast.
                    }

                    @Override
                    public void onSuccess(Result result) {
                        Intent intent = new Intent(getActivity(), DuringRunActivity.class);
                        intent.putExtra("start", start);
                        intent.putExtra("end", end);
                        intent.putExtra("plan", "d");
                        intent.putExtra("distance", result.getRows().get(0).getElements().get(0).getDistance().getText());
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //make a toast.
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