package com.cs407.runtrackpro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class StatsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected final String[] months = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
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
            previousRunDistance.setText(formatDistance(Double.parseDouble(stats.getDistance())));
            previousRunTime.setText(formatTime(getTimeInSeconds(stats)));
            previousRunPace.setText(formatPace(getPace(stats)));
        } else {
            // Fail-safe in case there are no runs. Should never get here.
            Log.d("StatsFragment", "No runs found");
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private LineData getData() {
        // comment this out if using dummy data
        ArrayList<Entry> values = getMonthlyTotal();

        // Add dummy data
//        ArrayList<Entry> values = new ArrayList<>();
//        for (int i = 0; i < 7; i++) {
//            if (i == 3 || i == 4 || i == 5) {
//                values.add(new Entry(i, 0));
//                continue;
//            }
//            float val = (float) (Math.random() * 1200) + 3;
//            values.add(new Entry(i, val));
//        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "Total Time Each Month");
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
        leftAxis.setLabelCount(6, true);
        leftAxis.setGranularity(1f);

        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter((value, axis) -> months[(int) value % months.length]);

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
        return String.format("%dh %02dm", hours, minutes);
    }

    @SuppressLint("DefaultLocale")
    private String formatDistance(double totalDistance) {
        return String.format("%.2f", totalDistance) + " mi";
    }

    @SuppressLint("DefaultLocale")
    private String formatPace(double totalPace) {
        Log.d("StatsFragment", "formatPace: " + totalPace);
        int hours = (int) (totalPace / 3600);
        int minutes = (int) ((totalPace % 3600) / 60);
        return hours + ":" + String.format("%02d", minutes) + " /mi";
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

            totalTimeSeconds += getTimeInSeconds(HomeFragment.stats1.get(i));

            double pace = totalDistance > 0 ? totalTimeSeconds / totalDistance : 0;
            totalPace += pace;
        }

        ArrayList<String> averages = new ArrayList<>();
        averages.add(formatDistance((int) (totalDistance / size)));
        averages.add(formatTime((int) (totalTimeSeconds / size)));
        averages.add(formatPace((int) (totalPace / size)));

        return averages;
    }

    private ArrayList<Entry> getMonthlyTotal() {
        HashMap<Integer, Integer[]> monthlyTotal = new HashMap<>();

        double size = HomeFragment.stats1.size();
        for (int i = 0; i < size; i++) {
            String[] dateParts = HomeFragment.stats1.get(i).getDate().split(" ")[0].split("/");
            int month = Integer.parseInt(dateParts[0]);

            String[] timeParts = HomeFragment.stats1.get(i).getTime().split(":");
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int seconds = Integer.parseInt(timeParts[2]);

            // Add to the total time for the corresponding month
            if (!monthlyTotal.containsKey(month)) {
                monthlyTotal.put(month, new Integer[]{hours, minutes, seconds});
            } else {
                Integer[] existingTime = monthlyTotal.get(month);
                existingTime[0] += hours;
                existingTime[1] += minutes;
                existingTime[2] += seconds;
                monthlyTotal.put(month, existingTime);
            }
        }

        // Convert the HashMap to ArrayList<Entry>
        ArrayList<Entry> values = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Integer[] totalTimes = monthlyTotal.getOrDefault(month, new Integer[]{0, 0, 0});
            int totalSeconds = totalTimes[0] * 3600 + totalTimes[1] * 60 + totalTimes[2];
            values.add(new Entry(month - 1, totalSeconds));
        }

        return values;
    }


    private double getPace(Stats stats) {
        int totalTimeSeconds = 0;
        double distance = Double.parseDouble(stats.getDistance());

        // Split the time string into minutes and seconds
        String[] timeParts = stats.getTime().split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);

        // Convert time to seconds and add to totalTimeSeconds
        totalTimeSeconds += hours * 3600 + minutes * 60 + seconds;

        return distance > 0 ? (totalTimeSeconds / distance) : 0;
    }

    private int getTimeInSeconds(Stats stats) {
        String[] timeParts = stats.getTime().split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }
}
