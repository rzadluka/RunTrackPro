package com.cs407.runtrackpro;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class StatsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public StatsFragment() {}

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LineChart chart = (LineChart) view.findViewById(R.id.chart);

        LineData data = getData(36, 100);
        setupChart(chart, data, Color.TRANSPARENT);
//        chart.setData(data); chart.invalidate();

        if (HomeFragment.stats1.size() != 0) {
            TextView statsDetails = view.findViewById(R.id.statsDetails);
            TextView distance = view.findViewById(R.id.distance);
            TextView time = view.findViewById(R.id.time);
            TextView pace = view.findViewById(R.id.pace);
            int statId = HomeFragment.stats1.size() - 1;

            if (statId != -1) {
                Stats stats = HomeFragment.stats1.get(statId);
                statsDetails.setText("Your run on " + stats.getDate());
                ArrayList<Integer> averages = getAverages();
                distance.setText(averages.get(0).toString());
                time.setText(averages.get(1).toString());
                pace.setText(averages.get(2).toString());
            }
        } else {
            TextView previousRunDate = view.findViewById(R.id.previousRunDate);
            TextView previousRunDistance = view.findViewById(R.id.previousRunDistance);
            TextView previousRunTime = view.findViewById(R.id.previousRunTime);
            TextView previousRunPace = view.findViewById(R.id.previousRunPace);
            TextView previousRunElevDiff = view.findViewById(R.id.previousRunElevDiff);

            previousRunDate.setText("No runs yet");
            previousRunDistance.setText("0.0");
            previousRunTime.setText("0.0");
            previousRunPace.setText("0.0");
            previousRunElevDiff.setText("0.0");
        }
    }

    private LineData getData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");

        set1.setLineWidth(1.75f);
        set1.setCircleRadius(5f);
        set1.setCircleHoleRadius(2.5f);
        set1.setColor(Color.rgb(108,75,171));
        set1.setCircleColor(Color.rgb(108,75,171));
        set1.setDrawValues(false);

        // create a data object with the data sets
        return new LineData(set1);
    }

    private void setupChart(LineChart chart, LineData data, int color) {
        // enable touch gestures
        chart.setTouchEnabled(true);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false);

        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(color);
        chart.setDrawGridBackground(false);

        // add data
        chart.setData(data);

        // animate calls invalidate()...
        chart.animateX(2500);
    }

    private ArrayList<Integer> getAverages() {
        int totalDistance = 0;
        int totalTime = 0;
        int totalPace = 0;
        int size = HomeFragment.stats1.size();
        for (int i = 0; i < size; i++) {
            totalDistance += Integer.parseInt(HomeFragment.stats1.get(i).getDistance());
            totalTime += Integer.parseInt(HomeFragment.stats1.get(i).getTime());
            totalPace += Integer.parseInt(HomeFragment.stats1.get(i).getDistance())/Integer.parseInt(HomeFragment.stats1.get(i).getTime());
        }
        ArrayList<Integer> averages = new ArrayList<>();
        averages.add(totalDistance / size);
        averages.add(totalTime / size);
        averages.add(totalPace / size);
        return averages;
    }

}