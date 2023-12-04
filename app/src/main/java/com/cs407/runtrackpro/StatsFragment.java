package com.cs407.runtrackpro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StatsFragment extends Fragment {
    protected final String[] week = new String[] {
            "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    };
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public StatsFragment() {
    }

    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Chart creation
        LineChart chart = (LineChart) view.findViewById(R.id.chart);
        LineData data = getData();
        setupChart(chart, data);

        // TextView population
        if (!HomeFragment.stats1.isEmpty()) {
            // Get views
            TextView distance = view.findViewById(R.id.distance);
            TextView time = view.findViewById(R.id.time);
            TextView pace = view.findViewById(R.id.pace);
            TextView previousRunDate = view.findViewById(R.id.previousRunDate);
            TextView previousRunDistance = view.findViewById(R.id.previousRunDistance);
            TextView previousRunTime = view.findViewById(R.id.previousRunTime);
            TextView previousRunPace = view.findViewById(R.id.previousRunPace);

            // Populate average stats
            Stats stats = HomeFragment.stats1.get(HomeFragment.stats1.size() - 1);
            previousRunDate.setText("Your run on " + convertDateToDescription(stats.getDate()));
            ArrayList<String> averages = getAverages();
            distance.setText(averages.get(0));
            time.setText(averages.get(1));
            pace.setText(averages.get(2));

            // Populate previous run stats
            previousRunDistance.setText(stats.getDistance() + " mi");
            previousRunTime.setText(stats.getTime());
            previousRunPace.setText(stats.getSpeed() + " min/mi");
        } else {
            // Fail-safe in case there are no runs. Should never get here.
            Log.d("StatsFragment", "No runs found");
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private LineData getData() {
        ArrayList<Entry> values = new ArrayList<>();

//        double size = HomeFragment.stats1.size();
//        for (int i = 0; i < size; i++) {
//            // Split the time string into minutes and seconds
//            String[] timeParts = HomeFragment.stats1.get(i).getTime().split(":");
//            if (timeParts.length == 2) {
//                int minutes = Integer.parseInt(timeParts[0]);
//                int seconds = Integer.parseInt(timeParts[1]);
//
//                // Convert time to seconds and add to totalTimeSeconds
//                values.add(new Entry(i,  minutes * 60 + seconds));
//            }
//        }

        for (int i = 0; i < 7; i++) {
            if (i == 3 || i == 4 || i == 5) {
                values.add(new Entry(i, 0));
                continue;
            }
            float val = (float) (Math.random() * 1200) + 3;
            values.add(new Entry(i, val));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "Time For Each Run");
        set1.setLineWidth(1.75f);
        set1.setCircleRadius(5f);
        set1.setCircleHoleRadius(2.5f);
        set1.setColor(Color.rgb(108, 75, 171));
        set1.setCircleColor(Color.rgb(108, 75, 171));
        set1.setDrawValues(false);

        // create a data object with the data sets
        return new LineData(set1);
    }

    private void setupChart(LineChart chart, LineData data) {
        // Touch behavior
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false);

        // Y Axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setValueFormatter((value, axis) -> formatTime((int) value));

        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter((value, axis) -> week[(int) value % week.length]);

        // Chart styling
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setDrawGridBackground(false);

        // Add data
        chart.setData(data);
        chart.animateX(2500);
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int totalTimeSeconds) {
        int hours = totalTimeSeconds / 3600;
        int minutes = (totalTimeSeconds % 3600) / 60;
        return hours + "h " + String.format("%02d", minutes) + "m";
    }

    @SuppressLint("DefaultLocale")
    private String formatDistance(double totalDistance) {
        return String.format("%.2f", totalDistance) + " mi";
    }

    @SuppressLint("DefaultLocale")
    private String formatPace(double totalPace) {
        int minutes = (int) totalPace / 60;
        int seconds = (int) totalPace % 60;
        return minutes + ":" + String.format("%02d", seconds) + " min/mi";
    }

    public static String convertDateToDescription(String dateString) {
        try {
            // Parse the original date string
            SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
            Date date = originalFormat.parse(dateString);

            // Format the Date object into the desired description format
            SimpleDateFormat descriptionFormat = new SimpleDateFormat("MMMM d", Locale.US);
            assert date != null;
            return descriptionFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private ArrayList<String> getAverages() {
        double totalDistance = 0;
        int totalTimeSeconds = 0;
        double totalPace = 0;
        double size = HomeFragment.stats1.size();

        for (int i = 0; i < size; i++) {
            totalDistance += Double.parseDouble(HomeFragment.stats1.get(i).getDistance());

            // Split the time string into minutes and seconds
            String[] timeParts = HomeFragment.stats1.get(i).getTime().split(":");
            if (timeParts.length == 2) {
                int minutes = Integer.parseInt(timeParts[0]);
                int seconds = Integer.parseInt(timeParts[1]);

                // Convert time to seconds and add to totalTimeSeconds
                totalTimeSeconds += minutes * 60 + seconds;
            }

            double pace = totalDistance > 0 ? totalTimeSeconds / totalDistance : 0;
            totalPace += pace;
        }

        ArrayList<String> averages = new ArrayList<>();
        averages.add(formatDistance((int) (totalDistance / size)));
        averages.add(formatTime((int) (totalTimeSeconds / size)));
        averages.add(formatPace((int) (totalPace / size)));

        return averages;
    }

}